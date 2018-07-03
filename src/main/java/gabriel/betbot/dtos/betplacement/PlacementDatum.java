
package gabriel.betbot.dtos.betplacement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlacementDatum {

    @JsonProperty("Bookie")
    public String bookie;
    @JsonProperty("Message")
    public String message;
    @JsonProperty("PlacedSuccessfully")
    public Boolean placedSuccessfully;
    @JsonProperty("ReturnCode")
    public Integer returnCode;

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
     * @param placedSuccessfully
     * @param returnCode
     * @param betPlacementReference
     */
    public PlacementDatum(String bookie, String message, Boolean placedSuccessfully, Integer returnCode) {
        this.bookie = bookie;
        this.message = message;
        this.placedSuccessfully = placedSuccessfully;
        this.returnCode = returnCode;
    }

    @Override
    public String toString() {
        return "PlacementDatum{" + "bookie=" + bookie + ", message=" + message + ", placedSuccessfully=" + placedSuccessfully + ", returnCode=" + returnCode + '}';
    }

}
