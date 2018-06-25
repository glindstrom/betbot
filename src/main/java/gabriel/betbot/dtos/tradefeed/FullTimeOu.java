
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "BookieOdds",
    "Goal"
})
public class FullTimeOu {

    @JsonProperty("BookieOdds")
    public final String bookieOdds;
    @JsonProperty("Goal")
    public final String goal;

    public FullTimeOu(final String bookieOdds, final String goal) {
        this.bookieOdds = bookieOdds;
        this.goal = goal;
    }

    public FullTimeOu() {
        this.bookieOdds = null;
        this.goal = null;
    }

}
