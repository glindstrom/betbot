
package gabriel.betbot.dtos.betplacement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "PlacementData"
})
public class Result {

    @JsonProperty("PlacementData")
    public List<PlacementDatum> placementData = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * 
     * @param placementData
     */
    public Result(List<PlacementDatum> placementData) {
        super();
        this.placementData = placementData;
    }

    @Override
    public String toString() {
        return "Result{" + "placementData=" + placementData + '}';
    }

}
