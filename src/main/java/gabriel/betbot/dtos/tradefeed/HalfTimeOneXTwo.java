
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "BookieOdds"
})
public class HalfTimeOneXTwo {

    @JsonProperty("BookieOdds")
    public final String bookieOdds;

    public HalfTimeOneXTwo(final String bookieOdds) {
        this.bookieOdds = bookieOdds;
    }

    public HalfTimeOneXTwo() {
        this.bookieOdds = null;
    }

    @Override
    public String toString() {
        return "HalfTimeOneXTwo{" + "bookieOdds=" + bookieOdds + '}';
    }
    
}
