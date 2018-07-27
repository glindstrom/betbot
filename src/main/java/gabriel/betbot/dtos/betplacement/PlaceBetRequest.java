package gabriel.betbot.dtos.betplacement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author gabriel
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceBetRequest {

    @JsonProperty("MarketTypeId")
    public final Integer marketTypeId;
    @JsonProperty("BookieOdds")
    public final String bookieOdds;
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
    @JsonProperty("Amount")
    public final Integer amount;
    @JsonProperty("PlaceBetId")
    public final String placeBetId;
    @JsonProperty("AcceptChangeOdds")
    public final Integer acceptChangeOdds;
    @JsonProperty("SportsType")
    public final Integer sportsType;

    public PlaceBetRequest() {
        this.marketTypeId = null;
        this.bookieOdds = null;
        this.isFullTime = null;
        this.gameId = null;
        this.gameType = null;
        this.oddsName = null;
        this.oddsFormat = null;
        this.amount = null;
        this.placeBetId = null;
        this.acceptChangeOdds = null;
        this.sportsType = null;
    }

    public PlaceBetRequest(final Builder builder) {
        this.marketTypeId = builder.marketTypeId;
        this.bookieOdds = builder.bookieOdds;
        this.isFullTime = builder.isFullTime;
        this.gameId = builder.gameId;
        this.gameType = builder.gameType;
        this.oddsName = builder.oddsName;
        this.oddsFormat = builder.oddsFormat;
        this.amount = builder.amount;
        this.placeBetId = builder.placeBetId;
        this.acceptChangeOdds = builder.acceptChangeOdds;
        this.sportsType = builder.sportsType;
    }

    public static class Builder {

        public Integer marketTypeId;
        public String bookieOdds;
        public Integer isFullTime;
        public Long gameId;
        public String gameType;
        public String oddsName;
        public String oddsFormat;
        public Integer amount;
        public String placeBetId;
        public Integer acceptChangeOdds;
        public Integer sportsType;
        
        public Builder withMarketTypeId(final Integer marketTypeId) {
            this.marketTypeId = marketTypeId;
            return this;
        }

        public Builder withBookieOdds(final String bookieOdds) {
            this.bookieOdds = bookieOdds;
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

        public Builder withOddsFormat(final String oddsFormat) {
            this.oddsFormat = oddsFormat;
            return this;
        }

        public Builder withPlaceBetId(final String placeBetId) {
            this.placeBetId = placeBetId;
            return this;
        }

        public Builder withAmount(final Integer amount) {
            this.amount = amount;
            return this;
        }
        
        public Builder withAcceptChangeOdds(final Integer acceptChangeOdds) {
            this.acceptChangeOdds = acceptChangeOdds;
            return this;
        }
        
        public Builder withSportsType(final Integer sportsType) {
            this.sportsType = sportsType;
            return this;
        }

        public PlaceBetRequest build() {
            return new PlaceBetRequest(this);
        }
    }

    @Override
    public String toString() {
        return "PlaceBetRequest{" + "marketTypeId=" + marketTypeId + ", bookieOdds=" + bookieOdds + ", isFullTime=" + isFullTime + ", gameId=" + gameId + ", gameType=" + gameType + ", oddsName=" + oddsName + ", oddsFormat=" + oddsFormat + ", amount=" + amount + ", placeBetId=" + placeBetId + ", acceptChangeOdds=" + acceptChangeOdds + ", sportsType=" + sportsType + '}';
    }
}
