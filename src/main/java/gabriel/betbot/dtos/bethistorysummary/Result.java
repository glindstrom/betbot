
package gabriel.betbot.dtos.bethistorysummary;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {

    @JsonProperty("BetSummaries")
    public List<BetSummary> betSummaries = null;
    @JsonProperty("SubTotalCommision")
    public String subTotalCommision;
    @JsonProperty("SubTotalProfitAndLost")
    public String subTotalProfitAndLost;
    @JsonProperty("Total")
    public String total;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * 
     * @param total
     * @param subTotalProfitAndLost
     * @param subTotalCommision
     * @param betSummaries
     */
    public Result(List<BetSummary> betSummaries, String subTotalCommision, String subTotalProfitAndLost, String total) {
        this.betSummaries = betSummaries;
        this.subTotalCommision = subTotalCommision;
        this.subTotalProfitAndLost = subTotalProfitAndLost;
        this.total = total;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("betSummaries", betSummaries).append("subTotalCommision", subTotalCommision).append("subTotalProfitAndLost", subTotalProfitAndLost).append("total", total).toString();
    }

}
