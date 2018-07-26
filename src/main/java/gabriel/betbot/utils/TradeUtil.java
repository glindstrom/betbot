
package gabriel.betbot.utils;

import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.trades.Trade;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gabriel
 */
public class TradeUtil {
    
    public static boolean arbitrageOpportunityExists(final Trade trade) {
        Odds pinnacleOdds = getPinnacleOdds(trade.getBookieOdds());
        if (pinnacleOdds == null) {
            return false;
        }
        return pinnacleOdds.getOddsType() == OddsType.ONE_X_TWO ? 
                arbitrageExistsForTradeWithThreePossibleOutcomes(trade) : arbitrageExistsForTradeWithTwoPossibleOutcomes(trade);
    }
    
    private static BigDecimal getBest1Odds(final Map<String, Odds> bookieOdds) {
        Map<String, Odds> bookieToOdds = new HashMap(bookieOdds);
        bookieToOdds.remove("PIN");
        return bookieToOdds.values().stream()
                .map(Odds::getOdds1)
                .max(BigDecimal::compareTo)
                .orElse(null);
    }
    
    private static BigDecimal getBest2Odds(final Map<String, Odds> bookieOdds) {
        Map<String, Odds> bookieToOdds = new HashMap(bookieOdds);
        bookieToOdds.remove("PIN");
        return bookieToOdds.values().stream()
                .map(Odds::getOdds2)
                .max(BigDecimal::compareTo)
                .orElse(null);
    }
    
    private static BigDecimal getBestXOdds(final Map<String, Odds> bookieOdds) {
        Map<String, Odds> bookieToOdds = new HashMap(bookieOdds);
        bookieToOdds.remove("PIN");
        return bookieToOdds.values().stream()
                .map(Odds::getOddsX)
                .max(BigDecimal::compareTo)
                .orElse(null);
    }
    
    private static BigDecimal getPinnacle1Odds(final Map<String, Odds> bookieOdds) {
        Odds pinnacleOdds = getPinnacleOdds(bookieOdds);
        return pinnacleOdds != null ? pinnacleOdds.getOdds1() : null;
    }
    
    private static  BigDecimal getPinnacle2Odds(final Map<String, Odds> bookieOdds) {
        Odds pinnacleOdds = getPinnacleOdds(bookieOdds);
        return pinnacleOdds != null ? pinnacleOdds.getOdds2() : null;
    }
    
    private static BigDecimal getPinnacleXOdds(final Map<String, Odds> bookieOdds) {
        Odds pinnacleOdds = getPinnacleOdds(bookieOdds);
        return pinnacleOdds != null ? pinnacleOdds.getOddsX() : null;
    }
    
    private static Odds getPinnacleOdds(final Map<String, Odds> bookieOdds) {
        return bookieOdds.get("PIN");
    }

    private static boolean arbitrageExistsForTradeWithTwoPossibleOutcomes(final Trade trade) {
       BigDecimal best1Odds = getBest1Odds(trade.getBookieOdds());
       BigDecimal best2Odds = getBest2Odds(trade.getBookieOdds());
       if (best1Odds == null || best2Odds == null) {
           return false;
       }
       BigDecimal pinnacle1Odds = getPinnacle1Odds(trade.getBookieOdds());
       BigDecimal pinnacle2Odds = getPinnacle2Odds(trade.getBookieOdds());
       return hasNegativeMarginal(best1Odds, pinnacle2Odds) || hasNegativeMarginal(best2Odds, pinnacle1Odds);
    }

    private static boolean arbitrageExistsForTradeWithThreePossibleOutcomes(final Trade trade) {
       BigDecimal best1Odds = getBest1Odds(trade.getBookieOdds());
       BigDecimal best2Odds = getBest2Odds(trade.getBookieOdds());
       BigDecimal bestXOdds = getBestXOdds(trade.getBookieOdds());
       if (best1Odds == null || best2Odds == null || bestXOdds == null) {
           return false;
       }
       BigDecimal pinnacle1Odds = getPinnacle1Odds(trade.getBookieOdds());
       BigDecimal pinnacle2Odds = getPinnacle2Odds(trade.getBookieOdds());
       BigDecimal pinnacleXOdds = getPinnacleXOdds(trade.getBookieOdds());
       return hasNegativeMarginal(best1Odds, pinnacle2Odds, pinnacleXOdds) 
               || hasNegativeMarginal(best2Odds, pinnacle1Odds, pinnacleXOdds)
               || hasNegativeMarginal(bestXOdds, pinnacle1Odds, pinnacle2Odds);
    }

    private static boolean hasNegativeMarginal(final BigDecimal odds1, final BigDecimal odds2) {
        return MathUtil.isLessThanOne(MathUtil.inverse(odds1).add(MathUtil.inverse(odds2)));
    }
    
    private static boolean hasNegativeMarginal(final BigDecimal odds1, final BigDecimal odds2, final BigDecimal odds3) {
        return MathUtil.isLessThanOne(MathUtil.inverse(odds1).add(MathUtil.inverse(odds2)).add(MathUtil.inverse(odds3)));
    }
}
