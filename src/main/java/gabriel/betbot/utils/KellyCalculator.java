
package gabriel.betbot.utils;

import java.math.BigDecimal;

/**
 *
 * @author gabriel
 */
public class KellyCalculator {
        
    public static final BigDecimal SCALE_FACTOR = BigDecimal.valueOf(0.3);
    private static final int NUM_DECIMALS = 10;
    
    public static BigDecimal getKellyFraction(final BigDecimal odds, final BigDecimal trueOdds) {
        BigDecimal pWin = BigDecimal.ONE.divide(trueOdds, NUM_DECIMALS, BigDecimal.ROUND_HALF_UP);
        BigDecimal pLoss = BigDecimal.ONE.subtract(pWin);
        BigDecimal netOdds = odds.subtract(BigDecimal.ONE);
        BigDecimal nominator = netOdds.multiply(pWin).subtract(pLoss);
        return nominator.divide(netOdds, NUM_DECIMALS, BigDecimal.ROUND_HALF_UP).multiply(SCALE_FACTOR);
    }

}
