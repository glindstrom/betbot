package gabriel.betbot.trades;

import java.util.Map;
import org.joda.time.LocalDateTime;

/**
 *
 * @author gabriel
 */
public class Trade {

    private final Map<String, Odds> bookmakerOdds;
    private final long gameId;
    private final int marketTypeId;
    private final LocalDateTime startTime;
    private final boolean isFullTime;
    private final String handicap;
    private final String goal;
    private final String homeTeamName;
    private final String awayTeamName;

    private Trade(final Builder builder) {
        this.bookmakerOdds = builder.bookmakerOdds;
        this.gameId = builder.gameId;
        this.marketTypeId = builder.marketTypeId;
        this.startTime = builder.startTime;
        this.isFullTime = builder.isFullTime;
        this.handicap = builder.handicap;
        this.goal = builder.goal;
        this.homeTeamName = builder.homeTeamName;
        this.awayTeamName = builder.awayTeamName;
    }

    @Override
    public String toString() {
        return homeTeamName + " - " + awayTeamName;
    }
    
    

    public static class Builder {

        private Map<String, Odds> bookmakerOdds;
        private long gameId;
        private int marketTypeId;
        private LocalDateTime startTime;
        private boolean isFullTime;
        private String handicap;
        private String goal;
        private String homeTeamName;
        private String awayTeamName;

        public Builder(final Trade source) {
            this.bookmakerOdds = source.bookmakerOdds;
            this.gameId = source.gameId;
            this.marketTypeId = source.marketTypeId;
            this.startTime = source.startTime;
            this.isFullTime = source.isFullTime;
            this.handicap = source.handicap;
            this.goal = source.goal;
            this.homeTeamName = source.homeTeamName;
            this.awayTeamName = source.awayTeamName;
        }

        public Builder() {
            this.handicap = null;
            this.goal = null;
        }

        public Builder withBookmakerOdds(final Map<String, Odds> bookmakerOdds) {
            this.bookmakerOdds = bookmakerOdds;
            return this;
        }

        public Builder withGameId(final long gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder withMarketTypeId(final int marketTypeId) {
            this.marketTypeId = marketTypeId;
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

        public Builder withHandicap(final String handicap) {
            this.handicap = handicap;
            return this;
        }
        
        public Builder withGoal(final String goal) {
            this.goal = goal;
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

        public Trade build() {
            return new Trade(this);
        }

    }

}
