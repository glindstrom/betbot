package gabriel.betbot.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import gabriel.betbot.dtos.tradefeed.MatchGame;
import gabriel.betbot.dtos.tradefeed.TradeFeedDto;
import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.Trade;
import gabriel.betbot.utils.Client;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 *
 * @author gabriel
 */
public class TradeService {

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
        List<MatchGame> matchGames = tradeFeedDto.result.sports.get(0).matchGames.stream()
                .filter(matchGame -> matchGame.isActive == true && matchGame.willBeRemoved == false)
                .collect(Collectors.toList());
        List<Trade> trades = tradeFeedToTrades(ImmutableList.copyOf(matchGames));
        trades.forEach(System.out::println);
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

    private Map<String, Odds> extractHdpBookieOdds(final String bookies) {
        Map<String, Odds> bookieOddsMap = new HashMap();
        String[] stringArray = bookies.split(";");
        for (String s : stringArray) {
            String[] arr = s.split("=");
            if (arr[0].equalsIgnoreCase("BEST")) {
                continue;
            }
            String[] oddsArray = arr[1].split(",");
            Odds odds = new Odds.Builder()
                    .withHomeOdds(new BigDecimal(oddsArray[0]))
                    .withAwayOdds(new BigDecimal(oddsArray[1]))
                    .build();
            bookieOddsMap.put(arr[0], odds);
        }
        return ImmutableMap.copyOf(bookieOddsMap);
    }

    private Map<String, Odds> extractOneXTwoBookieOdds(final String bookies) {
        Map<String, Odds> bookieOddsMap = new HashMap();
        String[] stringArray = bookies.split(";");
        for (String s : stringArray) {
            String[] arr = s.split("=");
            if (arr[0].equalsIgnoreCase("BEST")) {
                continue;
            }
            String[] oddsArray = arr[1].split(",");
            Odds odds = new Odds.Builder()
                    .withHomeOdds(new BigDecimal(oddsArray[0]))
                    .withAwayOdds(new BigDecimal(oddsArray[1]))
                    .withDrawOdds(new BigDecimal(oddsArray[2]))
                    .build();
            bookieOddsMap.put(arr[0], odds);
        }
        return ImmutableMap.copyOf(bookieOddsMap);
    }

    private Map<String, Odds> extractOuBookieOdds(final String bookies) {
        Map<String, Odds> bookieOddsMap = new HashMap();
        String[] stringArray = bookies.split(";");
        for (String s : stringArray) {
            String[] arr = s.split("=");
            if (arr[0].equalsIgnoreCase("BEST")) {
                continue;
            }
            String[] oddsArray = arr[1].split(",");
            Odds odds = new Odds.Builder()
                    .withOverOdds(new BigDecimal(oddsArray[0]))
                    .withUnderOdds(new BigDecimal(oddsArray[1]))
                    .build();
            bookieOddsMap.put(arr[0], odds);
        }
        return ImmutableMap.copyOf(bookieOddsMap);
    }

    private void addHdpTrade(final MatchGame matchGame, final List<Trade> trades, final boolean isFullTime) {
        String bookieOdds = isFullTime ? matchGame.fullTimeHdp.bookieOdds : matchGame.halfTimeHdp.bookieOdds;
        if (Strings.isNullOrEmpty(bookieOdds)) {
            return;
        }
        String handicap = isFullTime ? matchGame.fullTimeHdp.handicap : matchGame.halfTimeHdp.handicap;

        Trade trade = getTradeBuilder(matchGame)
                .withBookmakerOdds(extractHdpBookieOdds(bookieOdds))
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
                .withBookmakerOdds(extractOneXTwoBookieOdds(bookieOdds))
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
                .withBookmakerOdds(extractOuBookieOdds(bookieOdds))
                .withIsFullTime(isFullTime)
                .build();
        trades.add(trade);
    }

}
