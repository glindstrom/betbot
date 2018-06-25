
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "BookieOdds",
    "Goal"
})
public class HalfTimeOu {

    @JsonProperty("BookieOdds")
    public final String bookieOdds;
    @JsonProperty("Goal")
    public final String goal;

    public HalfTimeOu(final String bookieOdds, final String goal) {
        this.bookieOdds = bookieOdds;
        this.goal = goal;
    }

    public HalfTimeOu() {
        this.bookieOdds = null;
        this.goal = null;
    }

}
