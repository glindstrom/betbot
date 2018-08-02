
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "BookieOdds",
    "Handicap"
})
public class FullTimeHdp {

    @JsonProperty("BookieOdds")
    public final String bookieOdds;
    @JsonProperty("Handicap")
    public final String handicap;

    public FullTimeHdp(final String bookieOdds, final String handicap) {
        this.bookieOdds = bookieOdds;
        this.handicap = handicap;
    }

    public FullTimeHdp() {
        this.bookieOdds = null;
        this.handicap = null;
    }

    @Override
    public String toString() {
        return "FullTimeHdp{" + "bookieOdds=" + bookieOdds + ", handicap=" + handicap + '}';
    }
  
}
