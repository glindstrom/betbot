
package gabriel.betbot.service;

import gabriel.betbot.bankroll.Bankroll;
import java.math.BigDecimal;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author gabriel
 */
@Named
public class BankrollService {
    
   private static final BigDecimal RESERVE = BigDecimal.valueOf(500);
    
   private final AsianOddsClient asianOddsClient;
   private Bankroll bankroll;

   @Inject
    public BankrollService(final AsianOddsClient asianOddsClient) {
        this.asianOddsClient = asianOddsClient;
        this.bankroll = null;
    }
   
     public BigDecimal totalBankroll() {
         if (bankroll == null) {
             bankroll = asianOddsClient.getBankroll();
         }
        return bankroll.getOutstanding().add(bankroll.getCredit()).add(RESERVE);
    }
     
    public void clear() {
        this.bankroll = null;
    }
    
    public static BigDecimal getTotal(final Bankroll bankroll) {
        return bankroll.getTotal().add(RESERVE);
    }
    
    public static String bankrollToString(final Bankroll bankroll) {
        return "Credit: " + bankroll.getCredit()
                + ", Outstanding: " + bankroll.getOutstanding()
                + ", Total bankroll: " + getTotal(bankroll)
                + ", TodayPnl: " + bankroll.getTodayPnL()
                + ", YesterdayPnl: " + bankroll.getYesterdayPnl();
    }
}
