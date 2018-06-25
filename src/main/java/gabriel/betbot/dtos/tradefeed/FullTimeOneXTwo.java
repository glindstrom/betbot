
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "BookieOdds"
})
public class FullTimeOneXTwo {

    @JsonProperty("BookieOdds")
    public final String bookieOdds;

    public FullTimeOneXTwo(final String bookieOdds) {
        this.bookieOdds = bookieOdds;
    }

    public FullTimeOneXTwo() {
        this.bookieOdds = null;
    }
    
}
