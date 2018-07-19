package gabriel.betbot.utils;

import java.math.BigDecimal;

/**
 *
 * @author gabriel
 */
public class BetUtil {

    public static boolean edgeIsGreaterThan(final BigDecimal edge, final BigDecimal minimum) {
        return edge.compareTo(minimum) > 0;
    }
    
    public static boolean edgeIsGreaterThanOrEqualTo(final BigDecimal edge, final BigDecimal minimum) {
        return edge.compareTo(minimum) >= 0;
    }

    public static boolean oddsAreLessThanOrEqualTo(final BigDecimal odds, final BigDecimal maximum) {
        return odds.compareTo(maximum) <= 0;
    }
    
    public static boolean edgeIsLessThanOrEqualTo(final BigDecimal odds, final BigDecimal maximum) {
        return odds.compareTo(maximum) <= 0;
    }

}
