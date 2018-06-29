package gabriel.betbot.trades;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.joda.time.LocalDateTime;
import org.jongo.marshall.jackson.oid.MongoId;

/**
 *
 * @author gabriel
 */
public class Bet {

    @MongoId
    private final ObjectId id;
    private final BigDecimal odds;
    private final BigDecimal trueOdds;
    private final BigDecimal pinnacleOdds;
    private final List<String> bookies;
    private final OddsType oddsType;
    private final OddsName oddsName;
    private final BigDecimal edge;
    private final boolean isFullTime;
    private final String homeTeamName;
    private final String awayTeamName;
    private final String betDescription;
    private final LocalDateTime startTime;
    private final long gameId;
    private final SportsType sportsType;
    private final int recommendedStake;

    @JsonCreator
    private Bet(
           @MongoId final ObjectId id,
           @JsonProperty("odds") final BigDecimal odds,
           @JsonProperty("trueOdds") final BigDecimal trueOdds,
           @JsonProperty("pinnacleOdds") final BigDecimal pinnacleOdds,
           @JsonProperty("bookies") final List<String> bookies,
           @JsonProperty("oddsType") final OddsType oddsType,
           @JsonProperty("oddsName") final OddsName oddsName,
           @JsonProperty("edge") final BigDecimal edge,
           @JsonProperty("isFullTime") final boolean isFullTime,
           @JsonProperty("homeTeamName") final String homeTeamName,
           @JsonProperty("awayTeamName") final String awayTeamName,
           @JsonProperty("betDescription") final String betDescription,
           @JsonProperty("startTime") final LocalDateTime startTime,
           @JsonProperty("gameId") final long gameId,
           @JsonProperty("sportsType") final SportsType sportsType,
           @JsonProperty("recommendedStake") final int recommendedStake) {
        this.id = id;
        this.odds = odds;
        this.trueOdds = trueOdds;
        this.pinnacleOdds = pinnacleOdds;
        this.bookies = bookies;
        this.oddsType = oddsType;
        this.oddsName = oddsName;
        this.edge = edge;
        this.isFullTime = isFullTime;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.betDescription = betDescription;
        this.startTime = startTime;
        this.gameId = gameId;
        this.sportsType = sportsType;
        this.recommendedStake = recommendedStake;
    }

    private Bet(final Builder builder) {
        this.id = builder.id;
        this.odds = builder.odds;
        this.trueOdds = builder.trueOdds;
        this.pinnacleOdds = builder.pinnacleOdds;
        this.bookies = builder.bookies;
        this.gameId = builder.gameId;
        this.startTime = builder.startTime;
        this.isFullTime = builder.isFullTime;
        this.homeTeamName = builder.homeTeamName;
        this.awayTeamName = builder.awayTeamName;
        this.oddsType = builder.oddsType;
        this.oddsName = builder.oddsName;
        this.edge = builder.edge;
        this.betDescription = builder.betDescription;
        this.sportsType = builder.sportsType;
        this.recommendedStake = builder.recommendedStake;
    }

    public BigDecimal getOdds() {
        return odds;
    }

    public BigDecimal getTrueOdds() {
        return trueOdds;
    }

    public BigDecimal getPinnacleOdds() {
        return pinnacleOdds;
    }

    public List<String> getBookies() {
        return ImmutableList.copyOf(bookies);
    }

    public OddsType getOddsType() {
        return oddsType;
    }

    public OddsName getOddsName() {
        return oddsName;
    }

    public BigDecimal getEdge() {
        return edge;
    }

    public boolean isIsFullTime() {
        return isFullTime;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public String getBetDescription() {
        return betDescription;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getGameId() {
        return gameId;
    }

    @Override
    public String toString() {
        return "Bet{" + "id=" + id + ", odds=" + odds + ", trueOdds=" + trueOdds + ", pinnacleOdds=" + pinnacleOdds + ", bookies=" + bookies + ", oddsType=" + oddsType + ", oddsName=" + oddsName + ", edge=" + edge + ", isFullTime=" + isFullTime + ", homeTeamName=" + homeTeamName + ", awayTeamName=" + awayTeamName + ", betDescription=" + betDescription + ", startTime=" + startTime + ", gameId=" + gameId + ", sportsType=" + sportsType + ", recommendedStake=" + recommendedStake + '}';
    }

    public static class Builder {

        private ObjectId id;
        private BigDecimal odds;
        private BigDecimal trueOdds;
        private BigDecimal pinnacleOdds;
        private List<String> bookies;
        private OddsType oddsType;
        private OddsName oddsName;
        private BigDecimal edge;
        private boolean isFullTime;
        private String homeTeamName;
        private String awayTeamName;
        private String betDescription;
        private LocalDateTime startTime;
        private long gameId;
        private SportsType sportsType;
        private int recommendedStake;

        public Builder(final Bet source) {
            this.id = source.id;
            this.odds = source.odds;
            this.trueOdds = source.trueOdds;
            this.pinnacleOdds = source.pinnacleOdds;
            this.bookies = source.bookies;
            this.oddsType = source.oddsType;
            this.oddsName = source.oddsName;
            this.edge = source.edge;
            this.isFullTime = source.isFullTime;
            this.homeTeamName = source.homeTeamName;
            this.awayTeamName = source.awayTeamName;
            this.betDescription = source.betDescription;
            this.startTime = source.startTime;
            this.gameId = source.gameId;
            this.sportsType = source.sportsType;
            this.recommendedStake = source.recommendedStake;
        }

        public Builder() {
        }

        public Builder withGameId(final long gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder withStartTime(final LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder withIsFullTime(final boolean isFullTime) {
            this.isFullTime = isFullTime;
            return this;
        }

        public Builder withHomeTeamName(final String homeTeamName) {
            this.homeTeamName = homeTeamName;
            return this;
        }

        public Builder withAwayTeamName(final String awayTeamName) {
            this.awayTeamName = awayTeamName;
            return this;
        }

        public Builder withTrueOdds(final BigDecimal trueOdds) {
            this.trueOdds = trueOdds;
            return this;
        }

        public Builder withBookies(final List<String> bookies) {
            this.bookies = bookies;
            return this;
        }

        public Builder withEdge(final BigDecimal edge) {
            this.edge = edge;
            return this;
        }

        public Builder withOdds(final BigDecimal odds) {
            this.odds = odds;
            return this;
        }

        public Builder withOddsType(final OddsType oddsType) {
            this.oddsType = oddsType;
            return this;
        }

        public Builder withOddsName(final OddsName oddsName) {
            this.oddsName = oddsName;
            return this;
        }

        public Builder withPinnacleOdds(final BigDecimal pinnacleOdds) {
            this.pinnacleOdds = pinnacleOdds;
            return this;
        }

        public Builder withBetDescription(final String description) {
            this.betDescription = description;
            return this;
        }

        public Builder withId(final ObjectId id) {
            this.id = id;
            return this;
        }
        
        public Builder withSportsType(final SportsType sportsType) {
            this.sportsType = sportsType;
            return this;
        }
        
        public Builder withRecommendedStake(final int recommendedStake) {
            this.recommendedStake = recommendedStake;
            return this;
        }

        public Bet build() {
            return new Bet(this);
        }

    }

}
