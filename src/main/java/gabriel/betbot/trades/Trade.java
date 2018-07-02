package gabriel.betbot.trades;

import com.google.common.collect.ImmutableMap;
import java.time.LocalDateTime;
import java.util.Map;

/**
 *
 * @author gabriel
 */
public class Trade {

    private final Map<String, Odds> bookieOdds;
    private final long gameId;
    private final long matchId;
    private final int marketTypeId;
    private final LocalDateTime startTime;
    private final boolean isFullTime;
    private final String handicap;
    private final String goal;
    private final String homeTeamName;
    private final String awayTeamName;
    private final Odds trueOdds;
    private final SportsType sportsType;
    private final Team favoured;

    private Trade(final Builder builder) {
        this.bookieOdds = builder.bookmakerOdds;
        this.gameId = builder.gameId;
        this.matchId = builder.matchId;
        this.marketTypeId = builder.marketTypeId;
        this.startTime = builder.startTime;
        this.isFullTime = builder.isFullTime;
        this.handicap = builder.handicap;
        this.goal = builder.goal;
        this.homeTeamName = builder.homeTeamName;
        this.awayTeamName = builder.awayTeamName;
        this.trueOdds = builder.trueOdds;
        this.sportsType = builder.sportsType;
        this.favoured = builder.favoured;
    }

    public Map<String, Odds> getBookieOdds() {
        return ImmutableMap.copyOf(bookieOdds);
    }

    public long getGameId() {
        return gameId;
    }

    public int getMarketTypeId() {
        return marketTypeId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public boolean isIsFullTime() {
        return isFullTime;
    }

    public String getHandicap() {
        return handicap;
    }

    public String getGoal() {
        return goal;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public Odds getTrueOdds() {
        return trueOdds;
    }
    
    public SportsType getSportsType() {
        return sportsType;
    }

    public Team getFavoured() {
        return favoured;
    }

    public long getMatchId() {
        return matchId;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(homeTeamName).append(" - ")
                .append(awayTeamName);
        sb.append(" ").append(startTime);
        sb.append(" true odds: ").append(trueOdds);
        bookieOdds.forEach((k, v) -> sb.append(" ").append(k).append(": ").append(v));
        
        return sb.toString();
    }
    
    

    public static class Builder {

        private Map<String, Odds> bookmakerOdds;
        private long gameId;
        private long matchId;
        private int marketTypeId;
        private LocalDateTime startTime;
        private boolean isFullTime;
        private String handicap;
        private String goal;
        private String homeTeamName;
        private String awayTeamName;
        private Odds trueOdds;
        private SportsType sportsType;
        private Team favoured;


        public Builder(final Trade source) {
            this.bookmakerOdds = source.bookieOdds;
            this.gameId = source.gameId;
            this.matchId = source.matchId;
            this.marketTypeId = source.marketTypeId;
            this.startTime = source.startTime;
            this.isFullTime = source.isFullTime;
            this.handicap = source.handicap;
            this.goal = source.goal;
            this.homeTeamName = source.homeTeamName;
            this.awayTeamName = source.awayTeamName;
            this.trueOdds = source.trueOdds;
            this.sportsType = source.sportsType;
            this.favoured = source.favoured;
        }

        public Builder() {
            this.handicap = null;
            this.goal = null;
            this.trueOdds = null;
        }

        public Builder withBookmakerOdds(final Map<String, Odds> bookmakerOdds) {
            this.bookmakerOdds = bookmakerOdds;
            return this;
        }

        public Builder withGameId(final long gameId) {
            this.gameId = gameId;
            return this;
        }
        
        public Builder withMatchId(final long matchId) {
            this.matchId = matchId;
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
        
        public Builder withTrueOdds(final Odds trueOdds) {
            this.trueOdds = trueOdds;
            return this;
        }
        
        public Builder withSportsType(final SportsType sportsType) {
            this.sportsType = sportsType;
            return this;
        }
        
        public Builder withFavoured(final Team favoured) {
            this.favoured = favoured;
            return this;
        }

        public Trade build() {
            return new Trade(this);
        }

    }

}
