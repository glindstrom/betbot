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
       getIfNull();
        return bankroll.getOutstanding().add(bankroll.getCredit()).add(RESERVE);
    }
    
    public BigDecimal getTodayPnL() {
        getIfNull();
        return bankroll.getTodayPnL();
    }
    
    public BigDecimal getYesterdayPnL() {
        getIfNull();
        return bankroll.getYesterdayPnl();
    }
    
    public BigDecimal getCredit() {
        getIfNull();
        return bankroll.getCredit();
    }
    
    private void getIfNull() {
        if (bankroll == null) {
            bankroll = asianOddsClient.getBankroll();
        }
    }

    public void clear() {
        this.bankroll = null;
    }

    public static BigDecimal getTotal(final Bankroll bankroll) {
        return bankroll.getTotal().add(RESERVE);
    }

    public String bankrollToString() {
        getIfNull();
        return "Credit: " + bankroll.getCredit()
                + ", Outstanding: " + bankroll.getOutstanding()
                + ", Total: " + getTotal(bankroll)
                + ", Today: " + bankroll.getTodayPnL()
                + ", Yesterday: " + bankroll.getYesterdayPnl();
    }
}
