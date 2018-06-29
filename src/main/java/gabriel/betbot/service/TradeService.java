package gabriel.betbot.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import gabriel.betbot.db.Mongo;
import gabriel.betbot.repositories.BetRepository;
import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsName;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.trades.Trade;
import gabriel.betbot.utils.Client;
import gabriel.betbot.utils.KellyCalculator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;

/**
 *
 * @author gabriel
 */
public class TradeService {

    private static final Logger LOG = LogManager.getLogger(TradeService.class.getName());
    private static final String PINNACLE = "PIN";
    private static final int NUM_DECIMALS_ODDS = 3;
    private static final int NUM_DECIMALS_EDGE = 4;
    private static final int NUM_DECIMALS_CALCULATON = 10;
    private static final Hours CLOSE_TO_START_HOURS = Hours.TWO;
    private static final Hours NORMAL_HOURS = Hours.SEVEN;
    private static final Hours EARLY_HOURS = Hours.hours(10);
    private static final BigDecimal MIN_EDGE_CLOSE_TO_START = BigDecimal.valueOf(0.01);
    private static final BigDecimal MIN_EDGE_NORMAL = BigDecimal.valueOf(0.02);
    private static final BigDecimal MIN_EDGE_EARLY = BigDecimal.valueOf(0.04);
    private static final BigDecimal MAX_FRACTION = BigDecimal.valueOf(0.01);

    private final AsianOddsClient asianOddsClient;
    private final BetRepository betRepository;
    private final BankrollService bankrollService;

    @Inject
    public TradeService(final AsianOddsClient asianOddsClient,
            final BetRepository betRepository,
            final BankrollService bankrollService) {
        this.asianOddsClient = asianOddsClient;
        this.betRepository = betRepository;
        this.bankrollService = bankrollService;
    }

    public static void main(String[] args) {
        Client client = new Client();
        AsianOddsClient asianOddsClient = new AsianOddsClient(client);
        BankrollService bankrollService = new BankrollService(asianOddsClient);
        BetRepository betRepo = new BetRepository(new Mongo());
        TradeService tradeService = new TradeService(asianOddsClient, betRepo, bankrollService);
        tradeService.doBets();

    }

    public void doBets() {
        doBets(asianOddsClient.getTrades());
    }

    private void doBets(final List<Trade> tradesList) {
        List<Trade> trades = tradesList.stream()
                .filter(trade -> trade.getBookieOdds().containsKey(PINNACLE) && trade.getBookieOdds().keySet().size() > 1)
                .collect(toList());
        if (trades.isEmpty()) {
            LOG.info("No potential trades found at this time");
        }
        List<Bet> bets = trades.stream()
                .map(this::addTrueOdds)
                .map(this::addEdges)
                .map(trade -> tradeToBets(trade))
                .flatMap(Collection::stream)
                .filter(hasPositiveEdge())
                .collect(toList());

        List<Bet> mergedBetsList = new ArrayList();
        bets.forEach(bet -> {
            Bet sameBet = getBetWithSameOdds(bet, mergedBetsList);
            if (sameBet != null) {
                Bet mergedBet = mergeBets(bet, sameBet);
                mergedBetsList.add(mergedBet);
            } else {
                mergedBetsList.add(bet);
            }
        });

        if (mergedBetsList.isEmpty()) {
            LOG.info("No positive edge bets found at this time");
        }
        mergedBetsList.stream()
                .map(bet -> calculateAndAddRecommendedStake(bet))
                .sorted(Comparator.comparing(Bet::getEdge).reversed())
                .forEach(bet -> {
                    LOG.info(bet.toString());
                    List<Bet> madeBets = betRepository.findByGameId(bet.getGameId());
                    if (!madeBets.isEmpty()) {
                        LOG.info("Bet has already been made on this game");
                    } else {
                        betRepository.save(bet);
                    }
                });
    }

    private Bet calculateAndAddRecommendedStake(final Bet bet) {
        BigDecimal bankroll = bankrollService.totalBankroll();
        int maxStake = bigDecimalToInt(bankroll.multiply(MAX_FRACTION));
        int optimalStake = bigDecimalToInt(bankroll.multiply(KellyCalculator.getKellyFraction(bet.getOdds(), bet.getTrueOdds())));
        return new Bet.Builder(bet)
                .withRecommendedStake(Math.min(maxStake, optimalStake))
                .build();
    }

    private static int bigDecimalToInt(final BigDecimal bigDecimal) {
        return bigDecimal.toBigInteger().intValueExact();
    }

    private static Bet mergeBets(final Bet bet1, final Bet bet2) {
        List<String> bookies = new ArrayList();
        bookies.addAll(bet1.getBookies());
        bookies.addAll(bet2.getBookies());
        return new Bet.Builder(bet1)
                .withBookies(bookies)
                .build();
    }

    private static Bet getBetWithSameOdds(final Bet bet, final List<Bet> bets) {
        return bets.stream()
                .filter(sameOddsForSameBet(bet))
                .findAny()
                .orElse(null);
    }

    private static Predicate<Bet> sameOddsForSameBet(Bet otherBet) {
        return bet -> {
            return bet.getOdds().compareTo(otherBet.getOdds()) == 0
                    && bet.getGameId() == otherBet.getGameId()
                    && bet.getOddsName() == otherBet.getOddsName();
        };
    }

    private Trade addTrueOdds(final Trade trade) {
        Odds edgeComparisonOdds = trade.getBookieOdds().get(PINNACLE);
        Odds trueOdds = calculateTrueOdds(edgeComparisonOdds);
        return new Trade.Builder(trade).withTrueOdds(trueOdds).build();
    }

    private Trade addEdges(final Trade trade) {
        Map<String, Odds> bookieOdds = trade.getBookieOdds().values().stream()
                .map(odds -> addEdge(odds, trade.getTrueOdds()))
                .collect(Collectors.toMap(Odds::getBookie, Function.identity()));
        return new Trade.Builder(trade)
                .withBookmakerOdds(bookieOdds)
                .build();
    }

    static Odds calculateTrueOdds(final Odds odds) {
        BigDecimal prob1 = BigDecimal.ONE.divide(odds.getOdds1(), NUM_DECIMALS_CALCULATON, BigDecimal.ROUND_HALF_UP);
        BigDecimal prob2 = BigDecimal.ONE.divide(odds.getOdds2(), NUM_DECIMALS_CALCULATON, BigDecimal.ROUND_HALF_UP);
        BigDecimal prob3 = odds.getOddsType() == OddsType.ONE_X_TWO ? BigDecimal.ONE.divide(odds.getOddsX(), NUM_DECIMALS_CALCULATON, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        BigDecimal margin = prob1.add(prob2).add(prob3).subtract(BigDecimal.ONE);
        BigDecimal weightFactor = odds.getOddsType() == OddsType.ONE_X_TWO ? BigDecimal.valueOf(3) : BigDecimal.valueOf(2);
        BigDecimal trueOddsValue1 = calculateTrueOddsValue(margin, weightFactor, odds.getOdds1());
        BigDecimal trueOddsValue2 = calculateTrueOddsValue(margin, weightFactor, odds.getOdds2());
        Odds.Builder oddsBuilder = new Odds.Builder(odds).withOdds1(trueOddsValue1).withOdds2(trueOddsValue2);
        if (odds.getOddsType() == OddsType.ONE_X_TWO) {
            BigDecimal trueOddsValueX = calculateTrueOddsValue(margin, weightFactor, odds.getOddsX());
            oddsBuilder = oddsBuilder.withDrawOdds(trueOddsValueX);
        }
        return oddsBuilder.build();
    }

    private static BigDecimal calculateTrueOddsValue(final BigDecimal margin, final BigDecimal weightFactor, final BigDecimal oddsValue) {
        BigDecimal numerator = weightFactor.multiply(oddsValue);
        BigDecimal denominator = weightFactor.subtract(margin.multiply(oddsValue));
        return numerator.divide(denominator, NUM_DECIMALS_CALCULATON, RoundingMode.HALF_UP);
    }

    private Odds addEdge(final Odds odds, final Odds trueOdds) {
        if (odds.getBookie().equals(PINNACLE)) {
            return odds;
        }
        BigDecimal edge1 = calculateEdge(odds.getOdds1(), trueOdds.getOdds1());
        BigDecimal edge2 = calculateEdge(odds.getOdds2(), trueOdds.getOdds2());

        Odds.Builder oddsBuilder = new Odds.Builder(odds)
                .withEdge1(edge1)
                .withEdge2(edge2);
        if (odds.getOddsType() == OddsType.ONE_X_TWO) {
            BigDecimal edgeX = calculateEdge(odds.getOddsX(), trueOdds.getOddsX());
            oddsBuilder = oddsBuilder.withEdgeX(edgeX);
        }
        return oddsBuilder.build();
    }

    static BigDecimal calculateEdge(final BigDecimal offeredOdds, final BigDecimal trueOdds) {
        BigDecimal edge = offeredOdds.divide(trueOdds, NUM_DECIMALS_CALCULATON, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
        return edge.setScale(NUM_DECIMALS_EDGE, BigDecimal.ROUND_HALF_UP);
    }

    private static Predicate<Bet> hasPositiveEdge() {
        return bet -> {
            return hasEdgeCloseToStart(bet)
                    || hasNormalEdge(bet)
                    || hasEarlyEdge(bet);
        };
    }

    private static boolean hasEdgeCloseToStart(final Bet bet) {
        if (!startsInLessThanNHours(bet.getStartTime(), CLOSE_TO_START_HOURS)) {
            return false;
        }
        return edgeIsGreaterThan(bet.getEdge(), MIN_EDGE_CLOSE_TO_START);
    }

    private static boolean hasNormalEdge(final Bet bet) {
        if (!startsInLessThanNHours(bet.getStartTime(), NORMAL_HOURS)) {
            return false;
        }
        return edgeIsGreaterThan(bet.getEdge(), MIN_EDGE_NORMAL);
    }

    private static boolean hasEarlyEdge(final Bet bet) {
        if (!startsInLessThanNHours(bet.getStartTime(), EARLY_HOURS)) {
            return false;
        }
        return edgeIsGreaterThan(bet.getEdge(), MIN_EDGE_EARLY);
    }

    private static boolean startsInLessThanNHours(final LocalDateTime startTime, final Hours hours) {
        LocalDateTime now = LocalDateTime.now();
        return Hours.hoursBetween(now, startTime).isLessThan(hours);
    }

    private static boolean edgeIsGreaterThan(final BigDecimal edge, final BigDecimal minimum) {
        return edge.compareTo(minimum) > 0;
    }

    private static List<Bet> tradeToBets(final Trade trade) {
        return trade.getBookieOdds().values().stream()
                .map(odds -> oddsToBets(odds, trade))
                .collect(Collectors.toList())
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    private static List<Bet> oddsToBets(final Odds odds, final Trade trade) {
        List<Bet> bets = new ArrayList();
        OddsName odds1Name = odds.getOddsType() == OddsType.OVER_UNDER ? OddsName.OVER_ODDS : OddsName.HOME_ODDS;
        OddsName odds2Name = odds.getOddsType() == OddsType.OVER_UNDER ? OddsName.UNDER_ODDS : OddsName.AWAY_ODDS;
        bets.add(createBet(trade, odds, odds1Name));
        bets.add(createBet(trade, odds, odds2Name));
        if (odds.getOddsType() == OddsType.ONE_X_TWO) {
            bets.add(createBet(trade, odds, OddsName.DRAW_ODDS));
        }
        return bets;
    }

    private static Bet createBet(final Trade trade, final Odds odds, final OddsName oddsName) {
        String description = null;
        if (!Strings.isNullOrEmpty(trade.getHandicap())) {
            description = trade.getHandicap();
        } else if (!Strings.isNullOrEmpty(trade.getGoal())) {
            description = trade.getGoal();
        }
        return new Bet.Builder()
                .withHomeTeamName(trade.getHomeTeamName())
                .withAwayTeamName(trade.getAwayTeamName())
                .withGameId(trade.getGameId())
                .withStartTime(trade.getStartTime())
                .withIsFullTime(trade.isIsFullTime())
                .withOdds(odds.getOdds(oddsName))
                .withEdge(odds.getEdge(oddsName))
                .withOddsType(odds.getOddsType())
                .withOddsName(oddsName)
                .withBookies(ImmutableList.of(odds.getBookie()))
                .withTrueOdds(trade.getTrueOdds().getOdds(oddsName))
                .withPinnacleOdds(trade.getBookieOdds().get(PINNACLE).getOdds(oddsName))
                .withBetDescription(description)
                .withSportsType(trade.getSportsType())
                .build();

    }

}
