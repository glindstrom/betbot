package gabriel.betbot.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import gabriel.betbot.dtos.tradefeed.MatchGame;
import gabriel.betbot.dtos.tradefeed.TradeFeedDto;
import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsName;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.trades.Trade;
import gabriel.betbot.utils.Client;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
    private static final int NUM_DECIMALS = 3;
    private static final Hours CLOSE_TO_START_HOURS = Hours.TWO;
    private static final Hours NORMAL_HOURS = Hours.SEVEN;
    private static final Hours EARLY_HOURS = Hours.hours(10);
    private static final BigDecimal MIN_EDGE_CLOSE_TO_START = BigDecimal.valueOf(0.01);
    private static final BigDecimal MIN_EDGE_NORMAL = BigDecimal.valueOf(0.02);
    private static final BigDecimal MIN_EDGE_EARLY = BigDecimal.valueOf(0.04);

    private final AsianOddsClient asianOddsClient;

    @Inject
    public TradeService(final AsianOddsClient asianOddsClient) {
        this.asianOddsClient = asianOddsClient;
    }

    public void doFootballBets() {
        TradeFeedDto tradeFeedDto = asianOddsClient.getFootballFeeds();
        doBets(tradeFeedDto);
    }

    public void doBasbetballBets() {
        TradeFeedDto tradeFeedDto = asianOddsClient.getBasketballFeeds();
        doBets(tradeFeedDto);
    }

    public static void main(String[] args) {
        Client client = new Client();
        AsianOddsClient asianOddsClient = new AsianOddsClient(client);
        TradeService tradeService = new TradeService(asianOddsClient);
        LOG.info("Fetching football odds");
        tradeService.doFootballBets();
        LOG.info("Fetching basketball odds");
        tradeService.doBasbetballBets();
    }

    public void doBets(final TradeFeedDto tradeFeedDto) {
        if (tradeFeedDto.result == null || tradeFeedDto.result.sports == null || tradeFeedDto.result.sports.isEmpty()) {
            LOG.info("No trades were found at this time");
            return;
        }
        List<MatchGame> matchGames = tradeFeedDto.result.sports.get(0).matchGames.stream()
                .filter(matchGame -> matchGame.isActive == true && matchGame.willBeRemoved == false)
                .collect(Collectors.toList());
        List<Trade> trades = tradeFeedToTrades(ImmutableList.copyOf(matchGames)).stream()
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
        mergedBetsList.forEach(System.out::println);
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

    private List<Trade> tradeFeedToTrades(final List<MatchGame> matchGames) {
        List<Trade> trades = new ArrayList();
        matchGames.stream().forEach((matchGame) -> {
            addHdpTrade(matchGame, trades, true);
            addOneXTwoTrade(matchGame, trades, true);
            addOuTrade(matchGame, trades, true);
        });
        return ImmutableList.copyOf(trades);
    }

    private Map<String, Odds> extractBookieOdds(final String bookies, final OddsType oddsType) {
        Map<String, Odds> bookieOddsMap = new HashMap();
        String[] stringArray = bookies.split(";");
        for (String s : stringArray) {
            String[] arr = s.split("=");
            String bookie = arr[0];
            if (bookie.equalsIgnoreCase("BEST")) {
                continue;
            }
            String[] oddsArray = arr[1].split(",");
            if (oddsArray.length < 2) {
                continue;
            }
            Odds odds = createOdds(bookie, oddsArray, oddsType);
            bookieOddsMap.put(bookie, odds);
        }
        return ImmutableMap.copyOf(bookieOddsMap);
    }

    private Odds createOdds(final String bookie, final String[] oddsArray, final OddsType oddsType) {
        Odds.Builder oddsBuilder = new Odds.Builder().withBookie(bookie);
        try {
            switch (oddsType) {
                case HANDICAP:
                    oddsBuilder = oddsBuilder.withHomeOdds(new BigDecimal(oddsArray[0]))
                            .withAwayOdds(new BigDecimal(oddsArray[1]))
                            .withOddsType(OddsType.HANDICAP);
                    break;
                case OVER_UNDER:
                    oddsBuilder = oddsBuilder.withOverOdds(new BigDecimal(oddsArray[0]))
                            .withUnderOdds(new BigDecimal(oddsArray[1]))
                            .withOddsType(OddsType.OVER_UNDER);
                    break;
                case ONE_X_TWO:
                    oddsBuilder = oddsBuilder.withHomeOdds(new BigDecimal(oddsArray[0]))
                            .withAwayOdds(new BigDecimal(oddsArray[1]))
                            .withDrawOdds(new BigDecimal(oddsArray[2]))
                            .withOddsType(OddsType.ONE_X_TWO);
                    break;
                default:
                    throw new AssertionError(oddsType.name());
            }
            return oddsBuilder.build();
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.error("Error creating odds, oddsArray {}, bookie: {}, oddsType: {}", oddsArray, bookie, oddsType);
            throw ex;
        }
    }

    private void addHdpTrade(final MatchGame matchGame, final List<Trade> trades, final boolean isFullTime) {
        String bookieOdds = isFullTime ? matchGame.fullTimeHdp.bookieOdds : matchGame.halfTimeHdp.bookieOdds;
        if (Strings.isNullOrEmpty(bookieOdds)) {
            return;
        }
        String handicap = isFullTime ? matchGame.fullTimeHdp.handicap : matchGame.halfTimeHdp.handicap;
        Map<String, Odds> bookieOddsMap = extractBookieOdds(bookieOdds, OddsType.HANDICAP);
        if (bookieOddsMap.isEmpty()) {
            return;
        }
        Trade trade = getTradeBuilder(matchGame)
                .withBookmakerOdds(bookieOddsMap)
                .withHandicap(handicap)
                .withIsFullTime(isFullTime)
                .build();
        trades.add(trade);
    }

    private void addOneXTwoTrade(final MatchGame matchGame, final List<Trade> trades, final boolean isFullTime) {
        String bookieOdds = isFullTime ? matchGame.fullTimeOneXTwo.bookieOdds : matchGame.halfTimeOneXTwo.bookieOdds;
        if (Strings.isNullOrEmpty(bookieOdds)) {
            return;
        }

        Map<String, Odds> bookieOddsMap = extractBookieOdds(bookieOdds, OddsType.ONE_X_TWO);
        if (bookieOddsMap.isEmpty()) {
            return;
        }

        Trade trade = getTradeBuilder(matchGame)
                .withBookmakerOdds(bookieOddsMap)
                .withIsFullTime(isFullTime)
                .build();
        trades.add(trade);
    }

    private Trade.Builder getTradeBuilder(final MatchGame matchGame) {
        return new Trade.Builder()
                .withGameId(matchGame.gameId)
                .withMarketTypeId(matchGame.marketTypeId)
                .withStartTime(matchGame.startTime)
                .withHomeTeamName(matchGame.homeTeam.name)
                .withAwayTeamName(matchGame.awayTeam.name);
    }

    private void addOuTrade(final MatchGame matchGame, final List<Trade> trades, final boolean isFullTime) {
        String bookieOdds = isFullTime ? matchGame.fullTimeOu.bookieOdds : matchGame.halfTimeOu.bookieOdds;
        if (Strings.isNullOrEmpty(bookieOdds)) {
            return;
        }
        Map<String, Odds> bookieOddsMap = extractBookieOdds(bookieOdds, OddsType.OVER_UNDER);
        if (bookieOddsMap.isEmpty()) {
            return;
        }
        String goal = isFullTime ? matchGame.fullTimeOu.goal : matchGame.halfTimeOu.goal;
        Trade trade = getTradeBuilder(matchGame)
                .withBookmakerOdds(bookieOddsMap)
                .withIsFullTime(isFullTime)
                .withGoal(goal)
                .build();
        trades.add(trade);
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
        BigDecimal prob1 = BigDecimal.ONE.divide(odds.getOdds1(), NUM_DECIMALS, BigDecimal.ROUND_HALF_UP);
        BigDecimal prob2 = BigDecimal.ONE.divide(odds.getOdds2(), NUM_DECIMALS, BigDecimal.ROUND_HALF_UP);
        BigDecimal prob3 = odds.getOddsType() == OddsType.ONE_X_TWO ? BigDecimal.ONE.divide(odds.getOddsX(), 3, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
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
        return numerator.divide(denominator, NUM_DECIMALS, RoundingMode.HALF_UP);
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

    private static BigDecimal calculateEdge(final BigDecimal offeredOdds, final BigDecimal trueOdds) {
        return offeredOdds.divide(trueOdds, NUM_DECIMALS, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
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
                .build();

    }

}
