
package gabriel.betbot.dtos.placementinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "AwayName",
    "AwayScore",
    "Bookie",
    "Currency",
    "GameType",
    "HDPorGoal",
    "HomeName",
    "HomeOrAwayOrDraw",
    "HomeScore",
    "IsFullTime",
    "LeagueName",
    "MarketTypeId",
    "MaximumAmount",
    "Message",
    "MinimumAmount",
    "OddPlacementId",
    "OddsFormat",
    "Price",
    "Rejected",
    "SportsType"
})
public class OddsPlacementDatum {

    @JsonProperty("AwayName")
    public String awayName;
    @JsonProperty("AwayScore")
    public Integer awayScore;
    @JsonProperty("Bookie")
    public String bookie;
    @JsonProperty("Currency")
    public String currency;
    @JsonProperty("GameType")
    public String gameType;
    @JsonProperty("HDPorGoal")
    public String hDPorGoal;
    @JsonProperty("HomeName")
    public String homeName;
    @JsonProperty("HomeOrAwayOrDraw")
    public String homeOrAwayOrDraw;
    @JsonProperty("HomeScore")
    public Integer homeScore;
    @JsonProperty("IsFullTime")
    public Boolean isFullTime;
    @JsonProperty("LeagueName")
    public String leagueName;
    @JsonProperty("MarketTypeId")
    public Integer marketTypeId;
    @JsonProperty("MaximumAmount")
    public Integer maximumAmount;
    @JsonProperty("Message")
    public Object message;
    @JsonProperty("MinimumAmount")
    public Integer minimumAmount;
    @JsonProperty("OddPlacementId")
    public String oddPlacementId;
    @JsonProperty("OddsFormat")
    public String oddsFormat;
    @JsonProperty("Price")
    public Double price;
    @JsonProperty("Rejected")
    public Boolean rejected;
    @JsonProperty("SportsType")
    public Integer sportsType;

    /**
     * No args constructor for use in serialization
     * 
     */
    public OddsPlacementDatum() {
    }

    /**
     * 
     * @param isFullTime
     * @param leagueName
     * @param gameType
     * @param homeScore
     * @param marketTypeId
     * @param maximumAmount
     * @param sportsType
     * @param oddPlacementId
     * @param homeOrAwayOrDraw
     * @param currency
     * @param message
     * @param price
     * @param oddsFormat
     * @param bookie
     * @param rejected
     * @param awayName
     * @param hDPorGoal
     * @param homeName
     * @param minimumAmount
     * @param awayScore
     */
    public OddsPlacementDatum(String awayName, Integer awayScore, String bookie, String currency, String gameType, String hDPorGoal, String homeName, String homeOrAwayOrDraw, Integer homeScore, Boolean isFullTime, String leagueName, Integer marketTypeId, Integer maximumAmount, Object message, Integer minimumAmount, String oddPlacementId, String oddsFormat, Double price, Boolean rejected, Integer sportsType) {
        super();
        this.awayName = awayName;
        this.awayScore = awayScore;
        this.bookie = bookie;
        this.currency = currency;
        this.gameType = gameType;
        this.hDPorGoal = hDPorGoal;
        this.homeName = homeName;
        this.homeOrAwayOrDraw = homeOrAwayOrDraw;
        this.homeScore = homeScore;
        this.isFullTime = isFullTime;
        this.leagueName = leagueName;
        this.marketTypeId = marketTypeId;
        this.maximumAmount = maximumAmount;
        this.message = message;
        this.minimumAmount = minimumAmount;
        this.oddPlacementId = oddPlacementId;
        this.oddsFormat = oddsFormat;
        this.price = price;
        this.rejected = rejected;
        this.sportsType = sportsType;
    }

    @Override
    public String toString() {
        return "OddsPlacementDatum{" + "awayName=" + awayName + ", awayScore=" + awayScore + ", bookie=" + bookie + ", currency=" + currency + ", gameType=" + gameType + ", hDPorGoal=" + hDPorGoal + ", homeName=" + homeName + ", homeOrAwayOrDraw=" + homeOrAwayOrDraw + ", homeScore=" + homeScore + ", isFullTime=" + isFullTime + ", leagueName=" + leagueName + ", marketTypeId=" + marketTypeId + ", maximumAmount=" + maximumAmount + ", message=" + message + ", minimumAmount=" + minimumAmount + ", oddPlacementId=" + oddPlacementId + ", oddsFormat=" + oddsFormat + ", price=" + price + ", rejected=" + rejected + ", sportsType=" + sportsType + '}';
    }

}
