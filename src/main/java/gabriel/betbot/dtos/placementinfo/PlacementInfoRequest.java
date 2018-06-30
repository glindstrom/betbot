package gabriel.betbot.dtos.placementinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author gabriel
 */
public class PlacementInfoRequest {

    @JsonProperty("MarketTypeId")
    public final Integer marketTypeId;
    @JsonProperty("Bookies")
    public final List<String> bookies;
    @JsonProperty("IsFullTime")
    public final Integer isFullTime;
    @JsonProperty("GameId")
    public final Long gameId;
    @JsonProperty("GameType")
    public final String gameType;
    @JsonProperty("OddsName")
    public final String oddsName;
    @JsonProperty("OddsFormat")
    public final String oddsFormat;
    @JsonProperty("Timeout")
    public final Integer timeout;

    public PlacementInfoRequest() {
        this.marketTypeId = null;
        this.bookies = null;
        this.isFullTime = null;
        this.gameId = null;
        this.gameType = null;
        this.oddsName = null;
        this.oddsFormat = null;
        this.timeout = null;
    }

    public PlacementInfoRequest(final Builder builder) {
        this.marketTypeId = builder.marketTypeId;
        this.bookies = builder.bookies;
        this.isFullTime = builder.isFullTime;
        this.gameId = builder.gameId;
        this.gameType = builder.gameType;
        this.oddsName = builder.oddsName;
        this.oddsFormat = builder.oddsFormat;
        this.timeout = builder.timeout;
    }

    public static class Builder {

        public Integer marketTypeId;
        public List<String> bookies;
        public Integer isFullTime;
        public Long gameId;
        public String gameType;
        public String oddsName;
        public String oddsFormat;
        public Integer timeout;

        public Builder withMarketTypeId(final Integer marketTypeId) {
            this.marketTypeId = marketTypeId;
            return this;
        }

        public Builder withBookies(final List<String> bookies) {
            this.bookies = bookies;
            return this;
        }

        public Builder withIsFullTime(final Integer isFullTime) {
            this.isFullTime = isFullTime;
            return this;
        }

        public Builder withGameId(final Long gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder withGameType(final String gameType) {
            this.gameType = gameType;
            return this;
        }

        public Builder withOddsName(final String oddsName) {
            this.oddsName = oddsName;
            return this;
        }

        public Builder withTimeout(final Integer timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public PlacementInfoRequest build() {
            return new PlacementInfoRequest(this);
        }

    }
}
