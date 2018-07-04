
package gabriel.betbot.dtos.tradefeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Code",
    "Message",
    "Result"
})
public class TradeFeedDto {

    @JsonProperty("Code")
    public final Long code;
    @JsonProperty("Message")
    public final String message;
    @JsonProperty("Result")
    public final Result result;

    public TradeFeedDto(final Long code, final String message, final Result result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public TradeFeedDto() {
        this.code = null;
        this.message = null;
        this.result = null;
    }

    @Override
    public String toString() {
        return "TradeFeedDto{" + "code=" + code + ", message=" + message + ", result=" + result + '}';
    }
}
