
package gabriel.betbot.dtos.bethistorysummary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BetSummary {

    @JsonProperty("AwayName")
    public String awayName;
    @JsonProperty("AwayScore")
    public String awayScore;
    @JsonProperty("BetPlacementMessage")
    public String betPlacementMessage;
    @JsonProperty("BetPlacementReference")
    public String betPlacementReference;
    @JsonProperty("BetType")
    public String betType;
    @JsonProperty("Bookie")
    public String bookie;
    @JsonProperty("ClientType")
    public Integer clientType;
    @JsonProperty("Currency")
    public String currency;
    @JsonProperty("Favoured")
    public Integer favoured;
    @JsonProperty("FullTimeAwayScore")
    public String fullTimeAwayScore;
    @JsonProperty("FullTimeHomeScore")
    public String fullTimeHomeScore;
    @JsonProperty("GameType")
    public String gameType;
    @JsonProperty("HalfTimeAwayScore")
    public String halfTimeAwayScore;
    @JsonProperty("HalfTimeHomeScore")
    public String halfTimeHomeScore;
    @JsonProperty("HdpOrGoal")
    public String hdpOrGoal;
    @JsonProperty("HomeName")
    public String homeName;
    @JsonProperty("HomeScore")
    public String homeScore;
    @JsonProperty("InternalReferenceNumber")
    public String internalReferenceNumber;
    @JsonProperty("KickoffTime")
    public Integer kickoffTime;
    @JsonProperty("LeagueName")
    public String leagueName;
    @JsonProperty("Odds")
    public Double odds;
    @JsonProperty("OddsType")
    public String oddsType;
    @JsonProperty("Pnl")
    public Integer pnl;
    @JsonProperty("PnlInfo")
    public String pnlInfo;
    @JsonProperty("SportsType")
    public Integer sportsType;
    @JsonProperty("Stake")
    public Integer stake;
    @JsonProperty("Status")
    public String status;
    @JsonProperty("Term")
    public String term;
    @JsonProperty("TicketDate")
    public Integer ticketDate;

    /**
     * No args constructor for use in serialization
     * 
     */
    public BetSummary() {
    }

    /**
     * 
     * @param leagueName
     * @param gameType
     * @param favoured
     * @param sportsType
     * @param fullTimeAwayScore
     * @param odds
     * @param currency
     * @param halfTimeHomeScore
     * @param fullTimeHomeScore
     * @param stake
     * @param bookie
     * @param betType
     * @param oddsType
     * @param internalReferenceNumber
     * @param awayScore
     * @param betPlacementMessage
     * @param kickoffTime
     * @param homeScore
     * @param status
     * @param betPlacementReference
     * @param halfTimeAwayScore
     * @param ticketDate
     * @param pnl
     * @param term
     * @param awayName
     * @param pnlInfo
     * @param homeName
     * @param hdpOrGoal
     * @param clientType
     */
    public BetSummary(String awayName, String awayScore, String betPlacementMessage, String betPlacementReference, String betType, String bookie, Integer clientType, String currency, Integer favoured, String fullTimeAwayScore, String fullTimeHomeScore, String gameType, String halfTimeAwayScore, String halfTimeHomeScore, String hdpOrGoal, String homeName, String homeScore, String internalReferenceNumber, Integer kickoffTime, String leagueName, Double odds, String oddsType, Integer pnl, String pnlInfo, Integer sportsType, Integer stake, String status, String term, Integer ticketDate) {
        this.awayName = awayName;
        this.awayScore = awayScore;
        this.betPlacementMessage = betPlacementMessage;
        this.betPlacementReference = betPlacementReference;
        this.betType = betType;
        this.bookie = bookie;
        this.clientType = clientType;
        this.currency = currency;
        this.favoured = favoured;
        this.fullTimeAwayScore = fullTimeAwayScore;
        this.fullTimeHomeScore = fullTimeHomeScore;
        this.gameType = gameType;
        this.halfTimeAwayScore = halfTimeAwayScore;
        this.halfTimeHomeScore = halfTimeHomeScore;
        this.hdpOrGoal = hdpOrGoal;
        this.homeName = homeName;
        this.homeScore = homeScore;
        this.internalReferenceNumber = internalReferenceNumber;
        this.kickoffTime = kickoffTime;
        this.leagueName = leagueName;
        this.odds = odds;
        this.oddsType = oddsType;
        this.pnl = pnl;
        this.pnlInfo = pnlInfo;
        this.sportsType = sportsType;
        this.stake = stake;
        this.status = status;
        this.term = term;
        this.ticketDate = ticketDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("awayName", awayName).append("awayScore", awayScore).append("betPlacementMessage", betPlacementMessage).append("betPlacementReference", betPlacementReference).append("betType", betType).append("bookie", bookie).append("clientType", clientType).append("currency", currency).append("favoured", favoured).append("fullTimeAwayScore", fullTimeAwayScore).append("fullTimeHomeScore", fullTimeHomeScore).append("gameType", gameType).append("halfTimeAwayScore", halfTimeAwayScore).append("halfTimeHomeScore", halfTimeHomeScore).append("hdpOrGoal", hdpOrGoal).append("homeName", homeName).append("homeScore", homeScore).append("internalReferenceNumber", internalReferenceNumber).append("kickoffTime", kickoffTime).append("leagueName", leagueName).append("odds", odds).append("oddsType", oddsType).append("pnl", pnl).append("pnlInfo", pnlInfo).append("sportsType", sportsType).append("stake", stake).append("status", status).append("term", term).append("ticketDate", ticketDate).toString();
    }

}
