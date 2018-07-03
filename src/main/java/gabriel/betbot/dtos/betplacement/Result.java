
package gabriel.betbot.dtos.betplacement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {

    @JsonProperty("PlacementData")
    public List<PlacementDatum> placementData = null;
    @JsonProperty("BetPlacementReference")
    public String betPlacementReference;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * 
     * @param placementData
     * @param betPlacementReference
     */
    public Result(List<PlacementDatum> placementData, String betPlacementReference) {
        this.placementData = placementData;
        this.betPlacementReference = betPlacementReference;
    }

    @Override
    public String toString() {
        return "Result{" + "placementData=" + placementData + ", betPlacementReference=" + betPlacementReference + '}';
    }

}
