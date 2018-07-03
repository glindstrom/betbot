package gabriel.betbot.dtos.accountsummary;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 *
 * @author gabriel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    public final BigDecimal credit;
    public final BigDecimal outstanding;
    public final BigDecimal todayPnL;
    public final BigDecimal yesterdayPnL;

    @JsonCreator
    public Result(@JsonProperty("Credit") final BigDecimal credit,
            @JsonProperty("Outstanding") final BigDecimal outstanding,
            @JsonProperty("YesterdayPnL") final BigDecimal yesterdayPnl,
            @JsonProperty("TodayPnL") final BigDecimal todayPnL) {
        this.credit = credit;
        this.outstanding = outstanding;
        this.yesterdayPnL = yesterdayPnl;
        this.todayPnL = todayPnL;
    }

}
