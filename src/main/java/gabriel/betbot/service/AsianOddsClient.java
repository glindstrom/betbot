package gabriel.betbot.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import gabriel.betbot.bankroll.Bankroll;
import gabriel.betbot.dtos.accountsummary.AccountSummaryDto;
import gabriel.betbot.dtos.tradefeed.MatchGame;
import gabriel.betbot.dtos.tradefeed.TradeFeedDto;
import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.trades.SportsType;
import gabriel.betbot.trades.Trade;
import gabriel.betbot.utils.Client;
import gabriel.betbot.utils.JsonMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author gabriel
 */
@Named
public class AsianOddsClient {

    private static final String WEB_API_USERNAME = System.getenv("ASIANODDSWEBAPIUSERNAME");
    private static final String WEB_API_PASSWORD = System.getenv("ASIANODDSWEBAPIPASSWORD");
    private static final String BASE_URL = "https://webapi700.asianodds88.com/AsianOddsService";
    private static final String LOGIN_URL = BASE_URL + "/Login?username=" + WEB_API_USERNAME + "&password=" + WEB_API_PASSWORD;
    private static final String REGISTER_URL_SUFFIX = "/Register?username=" + WEB_API_USERNAME;
    private static final String TOKEN_HEADER_NAME = "AOToken";
    private static final String KEY_HEADER_NAME = "AOKey";
    private static final String ACCOUNT_SUMMARY_URL = BASE_URL + "/GetAccountSummary";

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(AsianOddsClient.class.getName());

    private final Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Header tokenHeader;

    @Inject
    public AsianOddsClient(final Client client) {
        this.client = client;
    }

    private LoginResponse login() {
        CloseableHttpResponse response = client.doGet(LOGIN_URL);
        return JsonMapper.jsonToObject(response, LoginResponse.class);
    }

    private void register(final LoginResponse loginResponse) {
        String registerUrl = loginResponse.result.url + REGISTER_URL_SUFFIX;
        this.tokenHeader = new BasicHeader(TOKEN_HEADER_NAME, loginResponse.result.token);
        Header keyHeader = new BasicHeader(KEY_HEADER_NAME, loginResponse.result.key);
        List<Header> headers = ImmutableList.of(tokenHeader, keyHeader);
        CloseableHttpResponse response = client.doGet(registerUrl, headers);
        try {
            System.out.println(response.getEntity().getContent().toString());
        } catch (IOException | UnsupportedOperationException ex) {
            Logger.getLogger(AsianOddsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loginAndRegister() {
        this.register(this.login());
    }

    public AccountSummaryDto getAccountSummary() {
        List<Header> headers = ImmutableList.of(tokenHeader);
        CloseableHttpResponse response = client.doGet(ACCOUNT_SUMMARY_URL, headers);
        return JsonMapper.jsonToObject(response, AccountSummaryDto.class);
    }
    
    public Bankroll getBankroll() {
        AccountSummaryDto accountSummary = getAccountSummary();
        return new Bankroll.Builder()
                .withCredit(accountSummary.result.credit)
                .withOutstanding(accountSummary.result.outstanding)
                .withTodayPnL(accountSummary.result.todayPnL)
                .build();
    }

    public List<Trade> getTrades() {
        List<Trade> trades = new ArrayList();
        TradeFeedDto footballTradeFeedDto = this.getFootballFeeds();
        JsonMapper.writeObjectToFile(footballTradeFeedDto, "/home/gabriel/Documents/Repos/betbot/ResponseData/football.json");
        trades.addAll(tradeFeedDtoToTrades(footballTradeFeedDto));
        TradeFeedDto basketTradeFeedDto = this.getBasketballFeeds();
        JsonMapper.writeObjectToFile(basketTradeFeedDto, "/home/gabriel/Documents/Repos/betbot/ResponseData/basket.json");
        trades.addAll(tradeFeedDtoToTrades(basketTradeFeedDto));
        
        return ImmutableList.copyOf(trades);
    }

    private static List<Trade> tradeFeedDtoToTrades(final TradeFeedDto tradeFeedDto) {
        if (tradeFeedDto.result == null || tradeFeedDto.result.sports == null || tradeFeedDto.result.sports.isEmpty()) {
            LOG.info("No trades were found at this time");
            return ImmutableList.of();
        }
        List<MatchGame> matchGames = tradeFeedDto.result.sports.get(0).matchGames.stream()
                .filter(matchGame -> matchGame.isActive == true && matchGame.willBeRemoved == false)
                .collect(Collectors.toList());
        List<Trade> trades = tradeFeedToTrades(ImmutableList.copyOf(matchGames), tradeFeedDto.result.sports.get(0).sportsType);

        return ImmutableList.copyOf(trades);
    }

    private static List<Trade> tradeFeedToTrades(final ImmutableList<MatchGame> matchGames, Integer sportsType) {
        List<Trade> trades = new ArrayList();
        matchGames.stream().forEach((matchGame) -> {
            addHdpTrade(matchGame, trades, true, sportsType);
            addOneXTwoTrade(matchGame, trades, true, sportsType);
            addOuTrade(matchGame, trades, true, sportsType);
        });
        return trades;
    }

    private static void addHdpTrade(final MatchGame matchGame, final List<Trade> trades, final boolean isFullTime, Integer sportsType) {
        String bookieOdds = isFullTime ? matchGame.fullTimeHdp.bookieOdds : matchGame.halfTimeHdp.bookieOdds;
        if (Strings.isNullOrEmpty(bookieOdds)) {
            return;
        }
        String handicap = isFullTime ? matchGame.fullTimeHdp.handicap : matchGame.halfTimeHdp.handicap;
        Map<String, Odds> bookieOddsMap = extractBookieOdds(bookieOdds, OddsType.HANDICAP);
        if (bookieOddsMap.isEmpty()) {
            return;
        }
        Trade trade = getTradeBuilder(matchGame, sportsType)
                .withBookmakerOdds(bookieOddsMap)
                .withHandicap(handicap)
                .withIsFullTime(isFullTime)
                .build();
        trades.add(trade);
    }

    private static void addOneXTwoTrade(final MatchGame matchGame, final List<Trade> trades, final boolean isFullTime, Integer sportsType) {
        String bookieOdds = isFullTime ? matchGame.fullTimeOneXTwo.bookieOdds : matchGame.halfTimeOneXTwo.bookieOdds;
        if (Strings.isNullOrEmpty(bookieOdds)) {
            return;
        }

        Map<String, Odds> bookieOddsMap = extractBookieOdds(bookieOdds, OddsType.ONE_X_TWO);
        if (bookieOddsMap.isEmpty()) {
            return;
        }

        Trade trade = getTradeBuilder(matchGame, sportsType)
                .withBookmakerOdds(bookieOddsMap)
                .withIsFullTime(isFullTime)
                .build();
        trades.add(trade);
    }

    private static void addOuTrade(final MatchGame matchGame, final List<Trade> trades, final boolean isFullTime, Integer sportsType) {
        String bookieOdds = isFullTime ? matchGame.fullTimeOu.bookieOdds : matchGame.halfTimeOu.bookieOdds;
        if (Strings.isNullOrEmpty(bookieOdds)) {
            return;
        }
        Map<String, Odds> bookieOddsMap = extractBookieOdds(bookieOdds, OddsType.OVER_UNDER);
        if (bookieOddsMap.isEmpty()) {
            return;
        }
        String goal = isFullTime ? matchGame.fullTimeOu.goal : matchGame.halfTimeOu.goal;
        Trade trade = getTradeBuilder(matchGame, sportsType)
                .withBookmakerOdds(bookieOddsMap)
                .withIsFullTime(isFullTime)
                .withGoal(goal)
                .build();
        trades.add(trade);
    }

    private static Trade.Builder getTradeBuilder(final MatchGame matchGame, Integer sportsType) {

        return new Trade.Builder()
                .withGameId(matchGame.gameId)
                .withSportsType(intToSportsType(sportsType))
                .withMarketTypeId(matchGame.marketTypeId)
                .withStartTime(matchGame.startTime)
                .withHomeTeamName(matchGame.homeTeam.name)
                .withAwayTeamName(matchGame.awayTeam.name);
    }

    private static SportsType intToSportsType(int sportsType) {
        if (sportsType == 1) {
            return SportsType.FOOTBALL;
        }
        if (sportsType == 2) {
            return SportsType.BASKETBALL;
        }
        throw new IllegalArgumentException("SportsType " + sportsType + " is not supported");
    }

    private static Map<String, Odds> extractBookieOdds(final String bookies, final OddsType oddsType) {
        Map<String, Odds> bookieOddsMap = new HashMap();
        String[] stringArray = bookies.split(";");
        for (String s : stringArray) {
            String[] arr = s.split("=");
            String bookie = arr[0];
            if (bookie.equalsIgnoreCase("BEST")) {
                continue;
            }
            String[] oddsArray = arr[1].split(",");
            if (oddsArray.length < 2 || (oddsType == OddsType.ONE_X_TWO && oddsArray.length < 3)) {
                continue;
            }
            Odds odds = createOdds(bookie, oddsArray, oddsType);
            bookieOddsMap.put(bookie, odds);
        }
        return ImmutableMap.copyOf(bookieOddsMap);
    }

    private static Odds createOdds(final String bookie, final String[] oddsArray, final OddsType oddsType) {
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

    public TradeFeedDto getFootballFeeds() {
        return getTradeFeeds(SportsType.FOOTBALL);
    }

    public TradeFeedDto getBasketballFeeds() {
        return getTradeFeeds(SportsType.BASKETBALL);
    }

    public TradeFeedDto getTradeFeeds(final SportsType sportsType) {
        if (tokenHeader == null) {
            loginAndRegister();
        }
        List<Header> headers = ImmutableList.of(tokenHeader);
        String feedUrl = BASE_URL + "/GetFeeds?marketTypeId=1&OddsFormat=OO&SportsType=" + sportsType.getId();
        CloseableHttpResponse response = client.doGet(feedUrl, headers);
        return JsonMapper.jsonToObject(response, TradeFeedDto.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class LoginResponse {

        public final Result result;

        @JsonCreator
        public LoginResponse(@JsonProperty("Result") final Result result) {
            this.result = result;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Result {

        public final String token;
        public final String url;
        public final String key;

        @JsonCreator
        public Result(@JsonProperty("Token") final String token,
                @JsonProperty("Url") final String url,
                @JsonProperty("Key") final String key) {
            this.token = token;
            this.url = url;
            this.key = key;
        }

    }
}
