
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Name",
    "RedCards",
    "Score"
})
public class AwayTeam {

    @JsonProperty("Name")
    public final String name;
    @JsonProperty("RedCards")
    public final Integer redCards;
    @JsonProperty("Score")
    public final Integer score;

    public AwayTeam(final String name, final Integer redCards, final Integer score) {
        this.name = name;
        this.redCards = redCards;
        this.score = score;
    }

    public AwayTeam() {
        this.name = null;
        this.redCards = null;
        this.score = null;
    } 
}
