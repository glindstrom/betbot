
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "BookieOdds",
    "Handicap"
})
public class HalfTimeHdp {

    @JsonProperty("BookieOdds")
    public final String bookieOdds;
    @JsonProperty("Handicap")
    public final String handicap;

    public HalfTimeHdp(final String bookieOdds, final String handicap) {
        this.bookieOdds = bookieOdds;
        this.handicap = handicap;
    }

    public HalfTimeHdp() {
        this.bookieOdds = null;
        this.handicap = null;
    }

}
