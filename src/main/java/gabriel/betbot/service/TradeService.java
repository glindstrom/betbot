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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
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

    private final AsianOddsClient asianOddsClient;

    @Inject
    public TradeService(final AsianOddsClient asianOddsClient) {
        this.asianOddsClient = asianOddsClient;
    }
    
    public static void main(String[] args) {
        Client client = new Client();
        AsianOddsClient asianOddsClient = new AsianOddsClient(client);
        TradeService tradeService = new TradeService(asianOddsClient);
        tradeService.doBets();
    }

    public void doBets() {
        TradeFeedDto tradeFeedDto = asianOddsClient.getFootballFeeds();
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
            LOG.info("No potential trades fount at this time");
        }
        trades.stream().map(this::addTrueOdds).forEach(System.out::println);
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
                        .withAwayOdds(new BigDecimal(oddsArray[1]));
                break;
            case OVER_UNDER:
                oddsBuilder = oddsBuilder.withOverOdds(new BigDecimal(oddsArray[0]))
                        .withUnderOdds(new BigDecimal(oddsArray[1]));
                break;
            case ONE_X_TWO:
                oddsBuilder = oddsBuilder.withHomeOdds(new BigDecimal(oddsArray[0]))
                    .withAwayOdds(new BigDecimal(oddsArray[1]))
                    .withDrawOdds(new BigDecimal(oddsArray[2]));
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

    private Odds calculateTrueOdds(final Odds odds) {
        BigDecimal prob1 = BigDecimal.ONE.divide(odds.getOdds1(), 3, BigDecimal.ROUND_HALF_UP);
        BigDecimal prob2 = BigDecimal.ONE.divide(odds.getOdds2(), 3, BigDecimal.ROUND_HALF_UP);
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
    
    private BigDecimal calculateTrueOddsValue(final BigDecimal margin, final BigDecimal weightFactor, final BigDecimal oddsValue) {
        BigDecimal numerator = weightFactor.multiply(oddsValue);
        BigDecimal denominator = weightFactor.subtract(margin.multiply(oddsValue));
        return numerator.divide(denominator, 3, RoundingMode.HALF_UP);
    }


}
