package gabriel.betbot.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import gabriel.betbot.repositories.BetRepository;
import gabriel.betbot.trades.AsianOddsBetResultUpdater;
import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.BetStatus;
import gabriel.betbot.trades.MarketType;
import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsName;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.trades.Trade;
import gabriel.betbot.utils.BetUtil;
import static gabriel.betbot.utils.BetUtil.edgeIsGreaterThan;
import static gabriel.betbot.utils.BetUtil.oddsAreLessThanOrEqualTo;
import static gabriel.betbot.utils.BetUtil.calculateTrueOdds;
import gabriel.betbot.utils.KellyCalculator;
import gabriel.betbot.utils.TradeUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author gabriel
 */
@Named
public class TradeService {

    public static final List<String> MAIN_LEAGUES = Arrays.asList(
            "SWITZERLAND SUPER LEAGUE",
            "USA MAJOR LEAGUE SOCCER",
            "MEXICO LIGA MX",
            "JAPAN FOOTBALL LEAGUE",
            "DENMARK SUPER LEAGUE",
            "CHINA SUPER LEAGUE",
            "SWEDEN ALLSVENSKAN",
            "SCOTLAND LEAGUE CUP",
            "FINLAND VEIKKAUSLIIGA",
            "BRAZIL SERIE A",
            "POLAND EKSTRAKLASA",
            "ENGLISH CHAMPIONSHIP",
            "ENGLISH LEAGUE ONE",
            "ENGLISH LEAGUE TWO",
            "FRANCE LIGUE 1",
            "FRANCE LIGUE 2",
            "GERMANY BUNDESLIGA 2",
            "HOLLAND EREDIVISIE",
            "IRELAND PREMIER DIVISION",
            "NORWAY ELITESERIEN",
            "RUSSIA PREMIER LEAGUE"
    );

    private static final Logger LOG = LogManager.getLogger(TradeService.class.getName());
    private static final String PINNACLE = "PIN";
    private static final int NUM_DECIMALS_EDGE = 4;
    private static final int NUM_DECIMALS_CALCULATON = 10;
    public static final long CLOSE_TO_START_MINUTES = 30;
    public static final long NORMAL_HOURS = 2;
    public static final long EARLY_HOURS = 10;
    private static final BigDecimal MIN_EDGE_CLOSE_TO_START = BigDecimal.valueOf(0.01);
    private static final BigDecimal MIN_EDGE_NORMAL = BigDecimal.valueOf(0.02);
    private static final BigDecimal MIN_EDGE_EARLY = BigDecimal.valueOf(0.05);
    private static final BigDecimal MAX_ODDS_CLOSE_TO_START = BigDecimal.valueOf(10);
    private static final BigDecimal MAX_ODDS_REGULAR = BigDecimal.valueOf(10);
    private static final BigDecimal MAX_FRACTION = BigDecimal.valueOf(0.01);
    private static final int BET_FREQUENCY_IN_MILLISECONDS = 60 * 1000;
    private static final int MAX_CLOSING_ODDS_MINUTES = 5;
    public static final String DRAW_NO_BET = "0.0";
    public static final String QUARTER_HANDICAP = "0-0.5";
    private static final int MINIMUM_MAXIMUM_AMOUNT = 1000;

    private final AsianOddsClient asianOddsClient;
    private final BetRepository betRepository;
    private final BankrollService bankrollService;
    private final AsianOddsBetResultUpdater updater;

    private BigDecimal todayProfitNLoss;
    private BigDecimal yesterDayProfitNLoss;
    private Map<Long, Odds> matchIdToTrue1X2Odds;
    private Map<Long, Odds> matchIdToTrue1X2OddsHalfTime;

    @Inject
    public TradeService(final AsianOddsClient asianOddsClient,
            final BetRepository betRepository,
            final BankrollService bankrollService,
            final AsianOddsBetResultUpdater updater) {
        this.asianOddsClient = asianOddsClient;
        this.betRepository = betRepository;
        this.bankrollService = bankrollService;
        this.updater = updater;
        this.todayProfitNLoss = BigDecimal.ZERO;
        this.yesterDayProfitNLoss = BigDecimal.ZERO;
    }

    @Scheduled(fixedDelay = BET_FREQUENCY_IN_MILLISECONDS)
    public void doBets() {
        matchIdToTrue1X2Odds = new HashMap();
        matchIdToTrue1X2OddsHalfTime = new HashMap();
        int numPlacedFootBallBets = doBets(asianOddsClient.getFootballTrades(MarketType.TODAY))
                + doBets(asianOddsClient.getFootballTrades(MarketType.EARLY));
        int numPlacedBasketBets = doBets(asianOddsClient.getBasketballTrades(MarketType.TODAY))
                + doBets(asianOddsClient.getBasketballTrades(MarketType.EARLY));
        LOG.info("Football bets placed: {}, basketball bets places: {}", numPlacedFootBallBets, numPlacedBasketBets);
        LOG.info(bankrollService.bankrollToString());
        updateResults(numPlacedFootBallBets + numPlacedBasketBets);
        asianOddsClient.clearMatchIdCache();
        bankrollService.clear();
        asianOddsClient.resetCurrentCredit();
    }

    private void updateResults(int numberOfBetsPlaced) {
        if (bankrollHasChanged() || numberOfBetsPlaced > 0) {
            LOG.info("Updating results");
            updater.updateResults(LocalDate.now());
            this.todayProfitNLoss = bankrollService.getTodayPnL();
            this.yesterDayProfitNLoss = bankrollService.getYesterdayPnL();
        }
    }

    private boolean bankrollHasChanged() {
        return (bankrollService.getTodayPnL().compareTo(this.todayProfitNLoss) != 0)
                || (bankrollService.getYesterdayPnL().compareTo(this.yesterDayProfitNLoss) != 0);
    }

    private int doBets(final List<Trade> tradesList) {
        List<Trade> trades = tradesList.stream()
                .filter(trade -> trade.getBookieOdds().containsKey(PINNACLE) && trade.getBookieOdds().keySet().size() > 1)
                .filter(noArbitrageOpportunityExists())
                .collect(toList());
        if (trades.isEmpty()) {
            LOG.info("No potential trades found at this time");
        }
        List<Bet> bets = trades.stream()
                .map(this::addTrueOdds)
                .map(this::addTrueDrawNoBetAndQuarterHandicapOdds)
                .filter(bet -> bet.getTrueOdds() != null)
                .map(this::addEdges)
                .map(trade -> tradeToBets(trade))
                .flatMap(Collection::stream)
                .filter(asianOddsHasTwoPossibleOutcomes())
                .map(bet -> addClosingOddsToExistingBet(bet))
                .filter(hasPositiveEdge())
                .collect(toList());

        if (bets.isEmpty()) {
            LOG.info("No positive edge bets found at this time");
        }
        Map<Long, Bet> matchIdToBet = bets.stream()
                .collect(Collectors.toMap(Bet::getMatchId, Function.identity(), (bet1, bet2) -> bet1.getEdge().compareTo(bet2.getEdge()) > 0 ? bet1 : bet2));
        List<Bet> placedBets = matchIdToBet.values().stream()
                .filter(betOnGameDoesNotExist())
                .map(bet -> calculateAndAddRecommendedStake(bet))
                .map(asianOddsClient::addPlacementInfo)
                .filter(bet -> bet.getMaximumAmount() > MINIMUM_MAXIMUM_AMOUNT)
                .map(bet -> setAmount(bet))
                .sorted(Comparator.comparing(Bet::getEdge).reversed())
                .filter(bet -> bet.getStatus() == BetStatus.OK)
                .map(asianOddsClient::placeBet)
                .map(betRepository::saveAndGet)
                .collect((Collectors.toList()));

        return placedBets.size();
    }

    private Trade addTrueDrawNoBetAndQuarterHandicapOdds(final Trade trade) {
        if (!isDrawNoBetOrQuarterHandicap(trade.getHandicap())) {
            return trade;
        }
        Odds true1x2Odds = trade.isIsFullTime() ? this.matchIdToTrue1X2Odds.get(trade.getMatchId()) : this.matchIdToTrue1X2OddsHalfTime.get(trade.getMatchId());
        if (true1x2Odds == null) {
            return trade;
        }
        Odds trueOdds = DRAW_NO_BET.equals(trade.getHandicap()) ? BetUtil.calcultateTrueDrawNoBetOdds(true1x2Odds) : BetUtil.calculateQuarterHandicapTrueOdds(true1x2Odds, trade.getFavoured());
        return new Trade.Builder(trade).withTrueOdds(trueOdds).build();
    }

    private static boolean isDrawNoBetOrQuarterHandicap(final String handicap) {
        return DRAW_NO_BET.equals(handicap) || QUARTER_HANDICAP.equals(handicap);
    }

    private Predicate<Bet> betOnGameDoesNotExist() {
        return bet -> !betOnGameAlreadyExists(bet);
    }

    private Bet addClosingOddsToExistingBet(final Bet bet) {
        LocalDateTime now = LocalDateTime.now();
        if (ChronoUnit.MINUTES.between(now, bet.getStartTime()) > MAX_CLOSING_ODDS_MINUTES) {
            return bet;
        }
        if (bet.getStartTime().isBefore(now)) {
            return bet;
        }
        List<Bet> placedBets = betRepository.findByMatchIdAndStatus(bet.getMatchId(), BetStatus.SUCCESS);
        Bet placedBet = placedBets.stream()
                .filter(b -> b.isIsFullTime() == bet.isIsFullTime()
                        && b.getOddsName() == bet.getOddsName()
                        && b.getOddsType() == bet.getOddsType()
                        && ((b.getBetDescription() == null && bet.getBetDescription() == null)
                        || (b.getBetDescription().equals(bet.getBetDescription()))))
                .findAny()
                .orElse(null);
        if (placedBet != null) {
            Bet betWithClosingOdds = new Bet.Builder(placedBet)
                    .withClosingOdds(bet.getPinnacleOdds())
                    .withTrueClosingOdds(bet.getTrueOdds())
                    .build();
            betRepository.save(betWithClosingOdds);
        }
        return bet;
    }

    private boolean betOnGameAlreadyExists(final Bet bet) {
        List<Bet> bets = betRepository.findByMatchIdAndStatus(bet.getMatchId(), BetStatus.SUCCESS);
        return !bets.isEmpty();
    }

    private static Bet setAmount(final Bet bet) {
        if (bet.getOptimalAmount() < bet.getMinimumAmount()) {
            LOG.info("Optimal amount is less than minumum amount {}", bet);
            return new Bet.Builder(bet)
                    .withStatus(BetStatus.CANCELLED)
                    .build();
        }
        int stake = Math.min(bet.getOptimalAmount(), bet.getMaximumAmount());
        return new Bet.Builder(bet)
                .withAmount(stake)
                .build();
    }

    private Bet calculateAndAddRecommendedStake(final Bet bet) {
        BigDecimal bankroll = bankrollService.totalBankroll();
        int maxStake = bigDecimalToInt(bankroll.multiply(MAX_FRACTION));
        int optimalStake = bigDecimalToInt(bankroll.multiply(KellyCalculator.getKellyFraction(bet.getOdds(), bet.getTrueOdds())));
        return new Bet.Builder(bet)
                .withOptimalAmount(Math.min(maxStake, optimalStake))
                .build();
    }

    private static int bigDecimalToInt(final BigDecimal bigDecimal) {
        return bigDecimal.toBigInteger().intValueExact();
    }

    private Trade addTrueOdds(final Trade trade) {
        if (isDrawNoBetOrQuarterHandicap(trade.getHandicap())) {
            return trade;
        }
        Odds edgeComparisonOdds = trade.getBookieOdds().get(PINNACLE);
        Odds trueOdds = calculateTrueOdds(edgeComparisonOdds);
        if (trueOdds.getOddsType() == OddsType.ONE_X_TWO) {
            if (trade.isIsFullTime()) {
                matchIdToTrue1X2Odds.put(trade.getMatchId(), trueOdds);
            } else {
                matchIdToTrue1X2OddsHalfTime.put(trade.getMatchId(), trueOdds);
            }
        }
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

    private static Predicate<Bet> asianOddsHasTwoPossibleOutcomes() {
        return bet -> Strings.isNullOrEmpty(bet.getBetDescription())
                || isDrawNoBetOrQuarterHandicap(bet.getBetDescription())
                || (bet.getBetDescription().endsWith(".5") && !bet.getBetDescription().contains("-"));
    }

    private static Predicate<Bet> hasPositiveEdge() {
        return bet -> {
            return hasEdgeCloseToStart(bet)
                    || hasNormalEdge(bet);
//                    || hasEarlyEdge(bet);
        };
    }

    private static boolean hasEdgeCloseToStart(final Bet bet) {
        if (!startsInLessThanNMinutes(bet.getStartTime(), CLOSE_TO_START_MINUTES)) {
            return false;
        }
        return oddsAreLessThanOrEqualTo(bet.getOdds(), MAX_ODDS_CLOSE_TO_START)
                && edgeIsGreaterThan(bet.getEdge(), MIN_EDGE_CLOSE_TO_START);
    }

    private static boolean hasNormalEdge(final Bet bet) {
        return oddsAreLessThanOrEqualTo(bet.getOdds(), MAX_ODDS_REGULAR)
                && edgeIsGreaterThan(bet.getEdge(), MIN_EDGE_NORMAL);
    }

    private static boolean hasEarlyEdge(final Bet bet) {
        if (!startsInLessThanNHours(bet.getStartTime(), EARLY_HOURS)) {
            return false;
        }
        return oddsAreLessThanOrEqualTo(bet.getOdds(), MAX_ODDS_REGULAR)
                && edgeIsGreaterThan(bet.getEdge(), MIN_EDGE_EARLY);
    }

    private static boolean startsInLessThanNHours(final LocalDateTime startTime, final long hours) {
        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.HOURS.between(now, startTime) < hours;
    }

    private static boolean startsInLessThanNMinutes(final LocalDateTime startTime, final long minutes) {
        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.MINUTES.between(now, startTime) < minutes;
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
                .withMatchId(trade.getMatchId())
                .withStartTime(trade.getStartTime())
                .withIsFullTime(trade.isIsFullTime())
                .withOdds(odds.getOdds(oddsName))
                .withEdge(odds.getEdge(oddsName))
                .withOddsType(odds.getOddsType())
                .withOddsName(oddsName)
                .withBookies(ImmutableSet.of(odds.getBookie()))
                .withTrueOdds(trade.getTrueOdds().getOdds(oddsName))
                .withPinnacleOdds(trade.getBookieOdds().get(PINNACLE).getOdds(oddsName))
                .withBetDescription(description)
                .withSportsType(trade.getSportsType())
                .withFavoured(trade.getFavoured())
                .withMarketType(MarketType.getById(trade.getMarketTypeId()))
                .withLeagueName(trade.getLeagueName())
                .build();

    }

    private Predicate<Trade> noArbitrageOpportunityExists() {
        return trade -> !TradeUtil.arbitrageOpportunityExists(trade);
    }

}
