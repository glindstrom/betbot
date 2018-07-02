package gabriel.betbot.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import gabriel.betbot.bankroll.Bankroll;
import gabriel.betbot.dtos.accountsummary.AccountSummaryDto;
import gabriel.betbot.dtos.betplacement.BetPlacementDto;
import gabriel.betbot.dtos.betplacement.PlaceBetRequest;
import gabriel.betbot.dtos.placementinfo.OddsPlacementDatum;
import gabriel.betbot.dtos.placementinfo.PlacementInfoDto;
import gabriel.betbot.dtos.placementinfo.PlacementInfoRequest;
import gabriel.betbot.dtos.tradefeed.MatchGame;
import gabriel.betbot.dtos.tradefeed.TradeFeedDto;
import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.BetStatus;
import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.trades.SportsType;
import gabriel.betbot.trades.Team;
import gabriel.betbot.trades.Trade;
import gabriel.betbot.utils.Client;
import gabriel.betbot.utils.DateUtil;
import gabriel.betbot.utils.JsonMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
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
    private static final String PLACEMENT_INFO_URL = BASE_URL + "/GetPlacementInfo";
    private static final String PLACE_BET_URL = BASE_URL + "/PlaceBet";
    private static final String TOKEN_HEADER_NAME = "AOToken";
    private static final String KEY_HEADER_NAME = "AOKey";
    private static final String ACCOUNT_SUMMARY_URL = BASE_URL + "/GetAccountSummary";
    private static final String ODDS_FORMAT = "00";
    private static final int MARKET_TYPE_TODAY = 1;

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(AsianOddsClient.class.getName());

    private final Client client;
    private Header tokenHeader;
    private long footballSince;
    private long basketballSince;

    @Inject
    public AsianOddsClient(final Client client) {
        this.client = client;
        this.footballSince = 0;
        this.basketballSince = 0;
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

    public Bet placeBet(final Bet bet) {
        PlaceBetRequest pbr = placeBetRequestFromBet(bet);
        BetPlacementDto betPlacementDto = getBetPlacementDto(pbr);
        if (betPlacementDto.code < 0) {
            return new Bet.Builder(bet)
                    .withStatus(BetStatus.FAIL)
                    .build();
        }
        return new Bet.Builder(bet)
                .withStatus(BetStatus.SUCCESS)
                .withBetPlacementReference(getBetPlacementReference(betPlacementDto))
                .withBookie(getBookie(betPlacementDto))
                .build();
    }

    private static String getBetPlacementReference(final BetPlacementDto bpd) {
        if (hasPlacementData(bpd)) {
            return bpd.result.placementData.get(0).betPlacementReference;
        }
        return null;
    }

    private static String getBookie(final BetPlacementDto bpd) {
        if (hasPlacementData(bpd)) {
            return bpd.result.placementData.get(0).bookie;
        }
        return null;
    }

    private static boolean hasPlacementData(final BetPlacementDto bpd) {
        return bpd.result != null && bpd.result.placementData != null && !bpd.result.placementData.isEmpty();
    }

    private PlaceBetRequest placeBetRequestFromBet(final Bet bet) {
        return new PlaceBetRequest.Builder()
                .withAcceptChangeOdds(0)
                .withAmount(bet.getAmount())
                .withBookieOdds(bookieOddsFromBet(bet))
                .withGameId(bet.getGameId())
                .withGameType(bet.getOddsType().getCode())
                .withOddsName(bet.getOddsName().getName())
                .withIsFullTime(bet.isIsFullTime() ? 1 : 0)
                .withMarketTypeId(MARKET_TYPE_TODAY)
                .withPlaceBetId(bet.getId().toHexString())
                .build();
    }

    private static String bookieOddsFromBet(final Bet bet) {
        return bet.getBookies().stream()
                .map(bookie -> bookie + ":" + bet.getOdds())
                .collect(Collectors.joining(","));
    }

    private BetPlacementDto getBetPlacementDto(final PlaceBetRequest pbr) {
        CloseableHttpResponse response = client.doPost(PLACE_BET_URL, ImmutableList.of(tokenHeader), JsonMapper.objectToString(pbr));
        return JsonMapper.jsonToObject(response, BetPlacementDto.class);
    }

    public Bet addPlacementInfo(final Bet bet) {
        PlacementInfoRequest pir = placementInfoRequestFromBet(bet);
        PlacementInfoDto placementInfoDto = getPlacementInfo(pir);
        if (placementInfoDto.code < 0) {
            return new Bet.Builder(bet)
                    .withStatus(BetStatus.FAIL)
                    .build();
        }
        List<OddsPlacementDatum> data = placementInfoDto.result.oddsPlacementData.stream()
                .filter(opd -> opd.rejected == false)
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            return new Bet.Builder(bet)
                    .withStatus(BetStatus.REJECTED)
                    .build();
        }
        int minAmount = data.stream()
                .map(d -> d.minimumAmount)
                .min(Comparator.comparing(Integer::valueOf)).get();
        int maxAmount = data.stream()
                .map(d -> d.maximumAmount)
                .max(Comparator.comparing(Integer::valueOf)).get();
        return new Bet.Builder(bet)
                .withStatus(BetStatus.OK)
                .withMinimumAmount(minAmount)
                .withMaximumAmount(maxAmount)
                .build();
    }

    private PlacementInfoDto getPlacementInfo(final PlacementInfoRequest pir) {
        CloseableHttpResponse response = client.doPost(PLACEMENT_INFO_URL, ImmutableList.of(tokenHeader), JsonMapper.objectToString(pir));
        return JsonMapper.jsonToObject(response, PlacementInfoDto.class);
    }

    private static PlacementInfoRequest placementInfoRequestFromBet(final Bet bet) {
        return new PlacementInfoRequest.Builder()
                .withBookies(String.join(",", bet.getBookies()))
                .withGameId(bet.getGameId())
                .withGameType(bet.getOddsType().getCode())
                .withMarketTypeId(bet.getMarketType().getId())
                .withOddsFormat(ODDS_FORMAT)
                .withOddsName(bet.getOddsName().getName())
                .withIsFullTime(bet.isIsFullTime() ? 1 : 0)
                .build();
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
        trades.addAll(getFootballTrades());
        trades.addAll(getBasketballTrades());

        return ImmutableList.copyOf(trades);
    }

    public List<Trade> getFootballTrades() {
        LOG.info("Fetching football trades");
        TradeFeedDto footballTradeFeedDto = this.getFootballFeeds();
        this.footballSince = footballTradeFeedDto.result != null ? footballTradeFeedDto.result.since : this.footballSince;
        JsonMapper.writeObjectToFile(footballTradeFeedDto, "/home/gabriel/Documents/Repos/betbot/ResponseData/football.json");

        return ImmutableList.copyOf(tradeFeedDtoToTrades(footballTradeFeedDto));
    }

    public List<Trade> getBasketballTrades() {
        LOG.info("Fetching basketball trades");
        TradeFeedDto basketTradeFeedDto = this.getBasketballFeeds();
        this.basketballSince = basketTradeFeedDto.result != null ? basketTradeFeedDto.result.since : this.basketballSince;
        JsonMapper.writeObjectToFile(basketTradeFeedDto, "/home/gabriel/Documents/Repos/betbot/ResponseData/basket.json");

        return ImmutableList.copyOf(tradeFeedDtoToTrades(basketTradeFeedDto));
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
                .withFavoured(matchGame.favoured == 2 ? Team.AWAY_TEAM : Team.HOME_TEAM)
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
                .withMatchId(matchGame.matchId)
                .withSportsType(intToSportsType(sportsType))
                .withMarketTypeId(matchGame.marketTypeId)
                .withStartTime(DateUtil.milliSecondsToLocalDateTime(matchGame.startTime))
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
            try {
                Odds odds = createOdds(bookie, oddsArray, oddsType);
                bookieOddsMap.put(bookie, odds);
            } catch (Exception e) {
                LOG.error("Error creating odds from: {}", bookies);
            }

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
        String feedUrl = BASE_URL + "/GetFeeds?marketTypeId=" + MARKET_TYPE_TODAY + "&OddsFormat=" + ODDS_FORMAT + "&SportsType=" + sportsType.getId();
        String since = "";
        if (sportsType == SportsType.FOOTBALL && this.footballSince > 0) {
            since = "&since=" + this.footballSince;
        } else if (sportsType == SportsType.BASKETBALL && this.basketballSince > 0) {
            since = "&since=" + this.basketballSince;
        }
        feedUrl += since;
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
