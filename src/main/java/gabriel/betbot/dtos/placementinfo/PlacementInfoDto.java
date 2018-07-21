
package gabriel.betbot.dtos.placementinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Code",
    "Result"
})
public class PlacementInfoDto {

    @JsonProperty("Code")
    public Integer code;
    @JsonProperty("Result")
    public Result result;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PlacementInfoDto() {
    }

    /**
     * 
     * @param result
     * @param code
     */
    public PlacementInfoDto(Integer code, Result result) {
        this.code = code;
        this.result = result;
    }

    @Override
    public String toString() {
        return "PlacementInfoDto{" + "code=" + code + ", result=" + result + '}';
    }

}
