
package gabriel.betbot.bankroll;

import java.math.BigDecimal;

/**
 *
 * @author gabriel
 */
public class Bankroll {
    private final BigDecimal credit;
    private final BigDecimal outstanding;
    private final BigDecimal todayPnL;
    private final BigDecimal yesterdayPnl;
    
    public Bankroll(final Builder builder) {
        this.credit = builder.credit;
        this.outstanding = builder.outstanding;
        this.todayPnL = builder.todayPnL;
        this.yesterdayPnl = builder.yesterdayPnL;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public BigDecimal getOutstanding() {
        return outstanding;
    }

    public BigDecimal getTodayPnL() {
        return todayPnL;
    }

    public BigDecimal getYesterdayPnl() {
        return yesterdayPnl;
    }
    
    public BigDecimal getTotal() {
        return this.credit.add(this.outstanding);
    }

    @Override
    public String toString() {
        return "Bankroll{" + "credit=" + credit + ", outstanding=" + outstanding + ", todayPnL=" + todayPnL + ", yesterdayPnl=" + yesterdayPnl + '}';
    }

    public static class Builder {
        private BigDecimal credit;
        private BigDecimal outstanding;
        private BigDecimal todayPnL;
        private BigDecimal yesterdayPnL;
        
        public Builder() {
        }
        
        public Builder(final Bankroll source) {
            this.credit = source.credit;
            this.outstanding = source.outstanding;
            this.todayPnL = source.todayPnL;
        }
        
        public Builder withCredit(final BigDecimal credit) {
            this.credit = credit;
            return this;
        }
        
        public Builder withOutstanding(final BigDecimal outstanding) {
            this.outstanding = outstanding;
            return this;
        }
        
        public Builder withTodayPnL(final BigDecimal todayPnL) {
            this.todayPnL = todayPnL;
            return this;
        }
        
        public Builder withYesterdayPnl(final BigDecimal yesterdayPnL) {
            this.yesterdayPnL = yesterdayPnL;
            return this;
        }
        
        public Bankroll build() {
            return new Bankroll(this);
        }
    }
}
