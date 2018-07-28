package gabriel.betbot.utils;

import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.trades.Team;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
        BigDecimal pWin = BigDecimal.ONE.divide(bet.getTrueClosingOdds(), NUM_DECIMALS_CALCULATON, BigDecimal.ROUND_HALF_UP);
        BigDecimal expectedReturn = bet.getOdds().multiply(pWin).subtract(BigDecimal.ONE);
        return expectedReturn.multiply(bet.getActualStake());
    }

    public static BigDecimal closingEdge(final Bet bet) {
        return MathUtil.divideXbyY(bet.getOdds(), bet.getTrueClosingOdds()).subtract(BigDecimal.ONE);
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

    public static BigDecimal unitProfit(final Bet bet) {
        BigDecimal profit;
        switch (bet.getPnlStatus()) {
            case WON:
                profit = bet.getOdds().subtract(BigDecimal.ONE);
                break;
            case HALF_WON:
                profit = bet.getOdds().subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(0.5));
                break;
            case HALF_LOST:
                profit = BigDecimal.valueOf(-0.5);
                break;
            case LOST:
                profit = BigDecimal.valueOf(-1);
                break;
            case STAKE_RETURNED:
                profit = BigDecimal.ZERO;
                break;
            default:
                throw new IllegalStateException("PnL status is null or not implemented");
        }
        
        return profit;
    }

    private static BigDecimal calculateTrueOddsValue(final BigDecimal margin, final BigDecimal weightFactor, final BigDecimal oddsValue) {
        BigDecimal numerator = weightFactor.multiply(oddsValue);
        BigDecimal denominator = weightFactor.subtract(margin.multiply(oddsValue));
        return numerator.divide(denominator, NUM_DECIMALS_CALCULATON, RoundingMode.HALF_UP);
    }

    public static Odds calcultateTrueDrawNoBetOdds(final Odds true1x2Odds) {
        BigDecimal homeOdds = divide(true1x2Odds.getHomeOdds(), true1x2Odds.getAwayOdds()).add(BigDecimal.ONE);
        BigDecimal awayOdds = divide(true1x2Odds.getAwayOdds(), true1x2Odds.getHomeOdds()).add(BigDecimal.ONE);

        return new Odds.Builder()
                .withHomeOdds(homeOdds)
                .withAwayOdds(awayOdds)
                .build();
    }

    private static BigDecimal divide(final BigDecimal dividend, final BigDecimal divisor) {
        return dividend.divide(divisor, NUM_DECIMALS_CALCULATON, RoundingMode.HALF_UP);
    }

    public static Odds calculateQuarterHandicapTrueOdds(final Odds true1x2odds, final Team favouredTeam) {
        BigDecimal homeOdds = favouredTeam == Team.HOME_TEAM ? calculateTrueQuarterHandicapAsianOddsForFavouredTeam(true1x2odds.getHomeOdds(), true1x2odds.getAwayOdds(), true1x2odds.getDrawOdds())
                : calculateTrueQuarterHandicapAsianOddsForNonFavouredTeam(true1x2odds.getHomeOdds(), true1x2odds.getAwayOdds(), true1x2odds.getDrawOdds());
        BigDecimal awayOdds = favouredTeam == Team.AWAY_TEAM ? calculateTrueQuarterHandicapAsianOddsForFavouredTeam(true1x2odds.getAwayOdds(), true1x2odds.getHomeOdds(), true1x2odds.getDrawOdds())
                : calculateTrueQuarterHandicapAsianOddsForNonFavouredTeam(true1x2odds.getAwayOdds(), true1x2odds.getHomeOdds(), true1x2odds.getDrawOdds());

        return new Odds.Builder()
                .withHomeOdds(homeOdds)
                .withAwayOdds(awayOdds)
                .build();
    }
    
    public static BigDecimal calculateProfit(final List<Bet> bets) {
        return bets.stream()
                .map(Bet::getPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public static BigDecimal calculateAverageExpectedROIwhenBetIsPlaced(final List<Bet> bets) {
         return bets.stream()
                .map(Bet::getEdge)
                .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(bets.size()), 4, BigDecimal.ROUND_HALF_UP);
    }

    private static BigDecimal calculateTrueQuarterHandicapAsianOddsForFavouredTeam(final BigDecimal oddsWin, final BigDecimal oddsLoss, final BigDecimal oddsDraw) {
        return divide(oddsWin, oddsLoss).add(divide(oddsWin, oddsDraw.multiply(BigDecimal.valueOf(2)))).add(BigDecimal.ONE);
    }

    private static BigDecimal calculateTrueQuarterHandicapAsianOddsForNonFavouredTeam(final BigDecimal oddsWin, final BigDecimal oddsLoss, final BigDecimal oddsDraw) {
        BigDecimal dividend = oddsWin.multiply(oddsDraw).multiply(BigDecimal.valueOf(2));
        BigDecimal divisor = oddsLoss.multiply(oddsDraw.multiply(BigDecimal.valueOf(2)).add(oddsWin));
        return divide(dividend, divisor).add(BigDecimal.ONE);
    }
}
