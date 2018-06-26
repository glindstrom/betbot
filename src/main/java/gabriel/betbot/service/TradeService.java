package gabriel.betbot.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import gabriel.betbot.dtos.tradefeed.MatchGame;
import gabriel.betbot.dtos.tradefeed.TradeFeedDto;
import gabriel.betbot.trades.Odds;
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
import java.util.stream.Stream;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gabriel
 */
public class TradeService {
    
    private static final Logger LOG = LogManager.getLogger(TradeService.class.getName());
    private static final String PINNACLE = "PIN";
    private static final int NUM_DECIMALS = 3;
    
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
        tradeService.doFootballBets();
        tradeService.doBasbetballBets();
    }
    
    public void doBets(final TradeFeedDto tradeFeedDto) {
        if (tradeFeedDto.result.sports.isEmpty()) {
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
        List<Trade> edgeTrades = trades.stream()
                .map(this::addTrueOdds)
                .map(this::addEdges)
                .filter(hasEdge())
                .collect(toList());
        
        if (edgeTrades.isEmpty()) {
            LOG.info("No positive edge trades found at this time");
        }
        edgeTrades.forEach(System.out::println);
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
            Odds odds = createOdds(bookie, oddsArray, oddsType);
            bookieOddsMap.put(bookie, odds);
        }
        return ImmutableMap.copyOf(bookieOddsMap);
    }
    
    private Odds createOdds(final String bookie, final String[] oddsArray, final OddsType oddsType) {
        Odds.Builder oddsBuilder = new Odds.Builder().withBookie(bookie);
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
    }
    
    private void addHdpTrade(final MatchGame matchGame, final List<Trade> trades, final boolean isFullTime) {
        String bookieOdds = isFullTime ? matchGame.fullTimeHdp.bookieOdds : matchGame.halfTimeHdp.bookieOdds;
        if (Strings.isNullOrEmpty(bookieOdds)) {
            return;
        }
        String handicap = isFullTime ? matchGame.fullTimeHdp.handicap : matchGame.halfTimeHdp.handicap;
        
        Trade trade = getTradeBuilder(matchGame)
                .withBookmakerOdds(extractBookieOdds(bookieOdds, OddsType.HANDICAP))
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
        
        Trade trade = getTradeBuilder(matchGame)
                .withBookmakerOdds(extractBookieOdds(bookieOdds, OddsType.ONE_X_TWO))
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
        
        Trade trade = getTradeBuilder(matchGame)
                .withBookmakerOdds(extractBookieOdds(bookieOdds, OddsType.OVER_UNDER))
                .withIsFullTime(isFullTime)
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
    
    private BigDecimal calculateEdge(final BigDecimal offeredOdds, final BigDecimal trueOdds) {
        return offeredOdds.divide(trueOdds, NUM_DECIMALS, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
    }
    
    private Predicate<Trade> hasEdge() {
        return trade -> trade.getBookieOdds().values().stream()
                .map(odds -> odds.getEdges())
                .flatMap(Collection::stream)
                .anyMatch(bigDecimal -> bigDecimal.compareTo(BigDecimal.valueOf(0.01)) > 0);
    }
    
}
