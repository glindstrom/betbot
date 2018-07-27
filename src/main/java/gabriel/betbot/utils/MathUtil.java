package gabriel.betbot.utils;

import java.math.BigDecimal;

/**
 *
 * @author gabriel
 */
public class MathUtil {

    private static final int NUM_DECIMALS = 10;

    public static BigDecimal inverse(final BigDecimal bigDecimal) {
        return (BigDecimal.ONE.divide(bigDecimal, NUM_DECIMALS, BigDecimal.ROUND_HALF_UP));
    }

    public static boolean isLessThanOne(final BigDecimal bigDecimal) {
        return bigDecimal.compareTo(BigDecimal.ONE) < 0;
    }

    public static BigDecimal divideXbyY(final BigDecimal x, final BigDecimal y) {
       return x.divide(y, NUM_DECIMALS, BigDecimal.ROUND_HALF_UP);
    }
    
    public static BigDecimal round(final BigDecimal odds) {
        return odds.setScale(3, BigDecimal.ROUND_HALF_UP);
    }

}
