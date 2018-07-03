
package gabriel.betbot.dtos.betplacement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
        this.code = code;
        this.result = result;
    }

    @Override
    public String toString() {
        return "BetPlacementDto{" + "code=" + code + ", result=" + result + '}';
    }

}
