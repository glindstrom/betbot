
package gabriel.betbot.dtos.tradefeed;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "MatchGames",
    "SportsType"
})
public class Sport {

    @JsonProperty("MatchGames")
    public final List<MatchGame> matchGames;
    @JsonProperty("SportsType")
    public final Integer sportsType;

    public Sport(final List<MatchGame> matchGames, final Integer sportsType) {
        this.matchGames = matchGames;
        this.sportsType = sportsType;
    }

    public Sport() {
        this.matchGames = null;
        this.sportsType = null;
    }

    @Override
    public String toString() {
        return "Sport{" + "matchGames=" + matchGames + ", sportsType=" + sportsType + '}';
    }
    
}
