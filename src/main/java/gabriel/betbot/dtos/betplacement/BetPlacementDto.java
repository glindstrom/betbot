
package gabriel.betbot.dtos.betplacement;

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
public class BetPlacementDto {

    @JsonProperty("Code")
    public Integer code;
    @JsonProperty("Result")
    public Result result;

    /**
     * No args constructor for use in serialization
     * 
     */
    public BetPlacementDto() {
    }

    /**
     * 
     * @param result
     * @param code
     */
    public BetPlacementDto(Integer code, Result result) {
        super();
        this.code = code;
        this.result = result;
    }

}
