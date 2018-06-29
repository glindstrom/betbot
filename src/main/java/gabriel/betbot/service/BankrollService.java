
package gabriel.betbot.service;

import gabriel.betbot.bankroll.Bankroll;
import java.math.BigDecimal;

/**
 *
 * @author gabriel
 */
public class BankrollService {
    
   private final AsianOddsClient asianOddsClient;
   private Bankroll bankroll;

    public BankrollService(final AsianOddsClient asianOddsClient) {
        this.asianOddsClient = asianOddsClient;
        this.bankroll = null;
    }
   
     public BigDecimal totalBankroll() {
         if (bankroll == null) {
             bankroll = asianOddsClient.getBankroll();
         }
        return bankroll.getOutstanding().add(bankroll.getCredit());
    }
     
    public void clear() {
        this.bankroll = null;
    }

}
