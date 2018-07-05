
package gabriel.betbot.dtos.bethistorysummary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BetHistorySummaryDto {

    @JsonProperty("Code")
    public Integer code;
    @JsonProperty("Message")
    public String message;
    @JsonProperty("Result")
    public Result result;

    /**
     * No args constructor for use in serialization
     * 
     */
    public BetHistorySummaryDto() {
    }

    /**
     * 
     * @param message
     * @param result
     * @param code
     */
    public BetHistorySummaryDto(Integer code, String message, Result result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("code", code).append("message", message).append("result", result).toString();
    }

}
