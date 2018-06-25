
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.joda.time.LocalDateTime;

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
    @JsonProperty("FullTimeHdp")
    public final FullTimeHdp fullTimeHdp;
    @JsonProperty("FullTimeOneXTwo")
    public final FullTimeOneXTwo fullTimeOneXTwo;
    @JsonProperty("FullTimeOu")
    public final FullTimeOu fullTimeOu;
    @JsonProperty("GameId")
    public final Integer gameId;
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
    public final Integer matchId;
    @JsonProperty("StartTime")
    public final LocalDateTime startTime;
    @JsonProperty("StartsOn")
    public final String startsOn;
    @JsonProperty("ToBeRemovedOn")
    public final Integer toBeRemovedOn;
    @JsonProperty("UpdatedDateTime")
    public final Integer updatedDateTime;
    @JsonProperty("WillBeRemoved")
    public final Boolean willBeRemoved;

    public MatchGame(AwayTeam awayTeam, 
            final Integer expectedLength, 
            final Integer favoured, 
            final FullTimeHdp fullTimeHdp, 
            final FullTimeOneXTwo fullTimeOneXTwo, 
            final FullTimeOu fullTimeOu, 
            final Integer gameId, 
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
            final Integer matchId, 
            final LocalDateTime startTime, 
            final String startsOn, 
            final Integer toBeRemovedOn, 
            final Integer updatedDateTime, 
            final Boolean willBeRemoved) {
        this.awayTeam = awayTeam;
        this.expectedLength = expectedLength;
        this.favoured = favoured;
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

    
}
