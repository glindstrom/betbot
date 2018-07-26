package gabriel.betbot.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author gabriel
 */
public class MathUtil {

    private static final int NUM_DECIMALS = 10;

    public static BigDecimal inverse(final BigDecimal bigDecimal) {
        return (BigDecimal.ONE.divide(bigDecimal, NUM_DECIMALS, RoundingMode.HALF_UP));
    }
    
    public static boolean isLessThanOne(final BigDecimal bigDecimal) {
        return bigDecimal.compareTo(BigDecimal.ONE) < 0;
    }

}
