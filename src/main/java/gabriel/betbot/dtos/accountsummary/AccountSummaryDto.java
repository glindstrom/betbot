
package gabriel.betbot.dtos.accountsummary;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author gabriel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountSummaryDto {
    
    public final Result result;

    @JsonCreator
    public AccountSummaryDto(@JsonProperty("Result") final Result result) {
        this.result = result;
    } 

}
