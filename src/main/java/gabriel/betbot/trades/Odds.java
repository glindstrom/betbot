package gabriel.betbot.trades;

import java.math.BigDecimal;

/**
 *
 * @author gabriel
 */
public class Odds {

    private final BigDecimal odds1;
    private final BigDecimal odds2;
    private final BigDecimal oddsX;

    private Odds(final Builder builder) {
        this.odds1 = builder.odds1;
        this.odds2 = builder.odds2;
        this.oddsX = builder.oddsX;
    }
    
    public BigDecimal getHomeOdds() {
        return odds1;
    }
    
    public BigDecimal getAwayOdds() {
        return odds2;
    }
    
    public BigDecimal getDrawOdds() {
        return oddsX;
    }
    
    public BigDecimal getOverOdds() {
        return odds1;
    }
    
    public BigDecimal getUnderOdds() {
        return odds2;
    }

    public static class Builder {

        private BigDecimal odds1;
        private BigDecimal odds2;
        private BigDecimal oddsX;

        public Builder() {
        }
        
        
        public Builder(final Odds source) {
            this.odds1 = source.odds1;
            this.odds2 = source.odds2;
            this.oddsX = source.oddsX;
        }
        
        public Builder withHomeOdds(final BigDecimal homeOdds) {
            this.odds1 = homeOdds;
            return this;
        }
        
        public Builder withAwayOdds(final BigDecimal awayOdds) {
            this.odds2 = awayOdds;
            return this;
        }
        
        public Builder withDrawOdds(final BigDecimal drawOdds) {
            this.oddsX = drawOdds;
            return this;
        }
        
        public Builder withOverOdds(final BigDecimal overOdds) {
            this.odds1 = overOdds;
            return this;
        }
        
        public Builder withUnderOdds(final BigDecimal underOdds) {
            this.odds2 = underOdds;
            return this;
        }
        
        public Odds build() {
            return new Odds(this);
        }
    }

}
