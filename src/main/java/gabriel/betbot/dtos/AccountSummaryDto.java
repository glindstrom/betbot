
package gabriel.betbot.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 *
 * @author gabriel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountSummaryDto {
    
    public final BigDecimal credit;
    public final BigDecimal outstanding;
    public final BigDecimal todayPnL;

    @JsonCreator
    public AccountSummaryDto(@JsonProperty("Credit") final BigDecimal credit, 
            @JsonProperty("Outstanding") final BigDecimal outstanding, 
            @JsonProperty("TodayPnL") final BigDecimal todayPnL) {
        this.credit = credit;
        this.outstanding = outstanding;
        this.todayPnL = todayPnL;
    }

    @Override
    public String toString() {
       return "Credit: " + credit;
    }
    
    
    
    

}
