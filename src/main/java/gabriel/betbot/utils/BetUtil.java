package gabriel.betbot.utils;

import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsType;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    
    public static Odds calculateTrueOdds(final Odds odds) {
        BigDecimal prob1 = BigDecimal.ONE.divide(odds.getOdds1(), NUM_DECIMALS_CALCULATON, BigDecimal.ROUND_HALF_UP);
        BigDecimal prob2 = BigDecimal.ONE.divide(odds.getOdds2(), NUM_DECIMALS_CALCULATON, BigDecimal.ROUND_HALF_UP);
        BigDecimal prob3 = odds.getOddsType() == OddsType.ONE_X_TWO ? BigDecimal.ONE.divide(odds.getOddsX(), NUM_DECIMALS_CALCULATON, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        BigDecimal margin = prob1.add(prob2).add(prob3).subtract(BigDecimal.ONE);
        BigDecimal weightFactor = odds.getOddsType() == OddsType.ONE_X_TWO ? BigDecimal.valueOf(3) : BigDecimal.valueOf(2);
        BigDecimal trueOddsValue1 = calculateTrueOddsValue(margin, weightFactor, odds.getOdds1());
        BigDecimal trueOddsValue2 = calculateTrueOddsValue(margin, weightFactor, odds.getOdds2());
        Odds.Builder oddsBuilder = new Odds.Builder(odds).withOdds1(trueOddsValue1).withOdds2(trueOddsValue2);
        if (odds.getOddsType() == OddsType.ONE_X_TWO) {
            BigDecimal trueOddsValueX = calculateTrueOddsValue(margin, weightFactor, odds.getOddsX());
            oddsBuilder = oddsBuilder.withDrawOdds(trueOddsValueX);
        }
        return oddsBuilder.build();
    }

    private static BigDecimal calculateTrueOddsValue(final BigDecimal margin, final BigDecimal weightFactor, final BigDecimal oddsValue) {
        BigDecimal numerator = weightFactor.multiply(oddsValue);
        BigDecimal denominator = weightFactor.subtract(margin.multiply(oddsValue));
        return numerator.divide(denominator, NUM_DECIMALS_CALCULATON, RoundingMode.HALF_UP);
    }
}
