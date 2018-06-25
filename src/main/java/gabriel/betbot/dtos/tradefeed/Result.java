
package gabriel.betbot.dtos.tradefeed;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Since",
    "Sports"
})
public class Result {

    @JsonProperty("Since")
    public final Long since;
    @JsonProperty("Sports")
    public final List<Sport> sports;

    public Result(final Long since, final List<Sport> sports) {
        this.since = since;
        this.sports = sports;
    }

    public Result() {
        this.since = null;
        this.sports = null;
    }
      
  
    
}
