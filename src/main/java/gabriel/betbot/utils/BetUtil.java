package gabriel.betbot.utils;

import gabriel.betbot.trades.Bet;
import java.math.BigDecimal;

/**
 *
 * @author gabriel
 */
public class BetUtil {

    private static final int NUM_DECIMALS_CALCULATON = 10;

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

    public static BigDecimal expectedProfit(final Bet bet) {
        BigDecimal pWin =  BigDecimal.ONE.divide(bet.getTrueClosingOdds(), NUM_DECIMALS_CALCULATON, BigDecimal.ROUND_HALF_UP);
        BigDecimal expectedReturn = bet.getOdds().multiply(pWin).subtract(BigDecimal.ONE);
        return expectedReturn.multiply(bet.getActualStake());
    }
}
