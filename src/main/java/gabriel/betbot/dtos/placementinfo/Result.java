
package gabriel.betbot.dtos.placementinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "OddsPlacementData"
})
public class Result {

    @JsonProperty("OddsPlacementData")
    public List<OddsPlacementDatum> oddsPlacementData = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * 
     * @param oddsPlacementData
     */
    public Result(List<OddsPlacementDatum> oddsPlacementData) {
        this.oddsPlacementData = oddsPlacementData;
    }

}
