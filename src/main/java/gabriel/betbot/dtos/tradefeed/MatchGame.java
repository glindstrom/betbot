
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "AwayTeam",
    "ExpectedLength",
    "Favoured",
    "FullTimeHdp",
    "FullTimeOneXTwo",
    "FullTimeOu",
    "GameId",
    "HalfTimeHdp",
    "HalfTimeOneXTwo",
    "HalfTimeOu",
    "HomeTeam",
    "InGameMinutes",
    "IsActive",
    "IsLive",
    "LeagueId",
    "LeagueName",
    "MarketType",
    "MarketTypeId",
    "MatchId",
    "StartTime",
    "StartsOn",
    "ToBeRemovedOn",
    "UpdatedDateTime",
    "WillBeRemoved"
})
public class MatchGame {

    @JsonProperty("AwayTeam")
    public final AwayTeam awayTeam;
    @JsonProperty("ExpectedLength")
    public final Integer expectedLength;
    @JsonProperty("Favoured")
    public final Integer favoured;
    @JsonProperty("FullTimeFavoured")
    public final Integer fullTimeFavoured;
    @JsonProperty("HalfTimeFavoured")
    public final Integer halfTimeFavoured;
    @JsonProperty("FullTimeHdp")
    public final FullTimeHdp fullTimeHdp;
    @JsonProperty("FullTimeOneXTwo")
    public final FullTimeOneXTwo fullTimeOneXTwo;
    @JsonProperty("FullTimeOu")
    public final FullTimeOu fullTimeOu;
    @JsonProperty("GameId")
    public final Long gameId;
    @JsonProperty("HalfTimeHdp")
    public final  HalfTimeHdp halfTimeHdp;
    @JsonProperty("HalfTimeOneXTwo")
    public final HalfTimeOneXTwo halfTimeOneXTwo;
    @JsonProperty("HalfTimeOu")
    public final HalfTimeOu halfTimeOu;
    @JsonProperty("HomeTeam")
    public final HomeTeam homeTeam;
    @JsonProperty("InGameMinutes")
    public final Integer inGameMinutes;
    @JsonProperty("IsActive")
    public final Boolean isActive;
    @JsonProperty("IsLive")
    public final Integer isLive;
    @JsonProperty("LeagueId")
    public final Integer leagueId;
    @JsonProperty("LeagueName")
    public final String leagueName;
    @JsonProperty("MarketType")
    public final String marketType;
    @JsonProperty("MarketTypeId")
    public final Integer marketTypeId;
    @JsonProperty("MatchId")
    public final Long matchId;
    @JsonProperty("StartTime")
    public final Long startTime;
    @JsonProperty("StartsOn")
    public final String startsOn;
    @JsonProperty("ToBeRemovedOn")
    public final Long toBeRemovedOn;
    @JsonProperty("UpdatedDateTime")
    public final Long updatedDateTime;
    @JsonProperty("WillBeRemoved")
    public final Boolean willBeRemoved;

    public MatchGame(AwayTeam awayTeam, 
            final Integer expectedLength, 
            final Integer favoured, 
            final Integer halfTimeFavoured,
            final Integer fullTImeFavoured,
            final FullTimeHdp fullTimeHdp, 
            final FullTimeOneXTwo fullTimeOneXTwo, 
            final FullTimeOu fullTimeOu, 
            final Long gameId, 
            final HalfTimeHdp halfTimeHdp, 
            final HalfTimeOneXTwo halfTimeOneXTwo, 
            final HalfTimeOu halfTimeOu, 
            final HomeTeam homeTeam, 
            final Integer inGameMinutes, 
            final Boolean isActive, 
            final Integer isLive, 
            final Integer leagueId, 
            final String leagueName, 
            final String marketType, 
            final Integer marketTypeId, 
            final Long matchId, 
            final Long startTime, 
            final String startsOn, 
            final Long toBeRemovedOn, 
            final Long updatedDateTime, 
            final Boolean willBeRemoved) {
        this.awayTeam = awayTeam;
        this.expectedLength = expectedLength;
        this.favoured = favoured;
        this.halfTimeFavoured = halfTimeFavoured;
        this.fullTimeFavoured = fullTImeFavoured;
        this.fullTimeHdp = fullTimeHdp;
        this.fullTimeOneXTwo = fullTimeOneXTwo;
        this.fullTimeOu = fullTimeOu;
        this.gameId = gameId;
        this.halfTimeHdp = halfTimeHdp;
        this.halfTimeOneXTwo = halfTimeOneXTwo;
        this.halfTimeOu = halfTimeOu;
        this.homeTeam = homeTeam;
        this.inGameMinutes = inGameMinutes;
        this.isActive = isActive;
        this.isLive = isLive;
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.marketType = marketType;
        this.marketTypeId = marketTypeId;
        this.matchId = matchId;
        this.startTime = startTime;
        this.startsOn = startsOn;
        this.toBeRemovedOn = toBeRemovedOn;
        this.updatedDateTime = updatedDateTime;
        this.willBeRemoved = willBeRemoved;
    }

    public MatchGame() {
        this.awayTeam = null;
        this.expectedLength = null;
        this.favoured = null;
        this.halfTimeFavoured = null;
        this.fullTimeFavoured = null;
        this.fullTimeHdp = null;
        this.fullTimeOneXTwo = null;
        this.fullTimeOu = null;
        this.gameId = null;
        this.halfTimeHdp = null;
        this.halfTimeOneXTwo = null;
        this.halfTimeOu = null;
        this.homeTeam = null;
        this.inGameMinutes = null;
        this.isActive = null;
        this.isLive = null;
        this.leagueId = null;
        this.leagueName = null;
        this.marketType = null;
        this.marketTypeId = null;
        this.matchId = null;
        this.startTime = null;
        this.startsOn = null;
        this.toBeRemovedOn = null;
        this.updatedDateTime = null;
        this.willBeRemoved = null;
    }

    @Override
    public String toString() {
        return "MatchGame{" + "awayTeam=" + awayTeam + ", expectedLength=" + expectedLength + ", favoured=" + favoured + ", fullTimeFavoured=" + fullTimeFavoured + ", halfTimeFavoured=" + halfTimeFavoured + ", fullTimeHdp=" + fullTimeHdp + ", fullTimeOneXTwo=" + fullTimeOneXTwo + ", fullTimeOu=" + fullTimeOu + ", gameId=" + gameId + ", halfTimeHdp=" + halfTimeHdp + ", halfTimeOneXTwo=" + halfTimeOneXTwo + ", halfTimeOu=" + halfTimeOu + ", homeTeam=" + homeTeam + ", inGameMinutes=" + inGameMinutes + ", isActive=" + isActive + ", isLive=" + isLive + ", leagueId=" + leagueId + ", leagueName=" + leagueName + ", marketType=" + marketType + ", marketTypeId=" + marketTypeId + ", matchId=" + matchId + ", startTime=" + startTime + ", startsOn=" + startsOn + ", toBeRemovedOn=" + toBeRemovedOn + ", updatedDateTime=" + updatedDateTime + ", willBeRemoved=" + willBeRemoved + '}';
    }

}
