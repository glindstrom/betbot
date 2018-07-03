
package gabriel.betbot.dtos.betplacement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "BetPlacementReference",
    "Bookie",
    "Message"
})
public class PlacementDatum {

    @JsonProperty("BetPlacementReference")
    public String betPlacementReference;
    @JsonProperty("Bookie")
    public String bookie;
    @JsonProperty("Message")
    public String message;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PlacementDatum() {
    }

    /**
     * 
     * @param message
     * @param bookie
     * @param betPlacementReference
     */
    public PlacementDatum(String betPlacementReference, String bookie, String message) {
        super();
        this.betPlacementReference = betPlacementReference;
        this.bookie = bookie;
        this.message = message;
    }

    @Override
    public String toString() {
        return "PlacementDatum{" + "betPlacementReference=" + betPlacementReference + ", bookie=" + bookie + ", message=" + message + '}';
    }

}
