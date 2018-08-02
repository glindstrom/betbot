
package gabriel.betbot.utils;

import com.google.common.collect.ImmutableMap;
import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.trades.Trade;
import static gabriel.betbot.utils.MathUtil.round;
import java.math.BigDecimal;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author gabriel
 */
public class TradeUtilTest {
    
    @Test
    public void marginalIsCorrect() {
        Odds pinnacleOdds = new Odds.Builder()
                .withHomeOdds(BigDecimal.valueOf(2.12))
                .withDrawOdds(BigDecimal.valueOf(3.2))
                .withAwayOdds(BigDecimal.valueOf(3.76))
                .withOddsType(OddsType.ONE_X_TWO)
                .build();
        
        Trade trade = new Trade.Builder()
                .withBookmakerOdds(ImmutableMap.of("PIN", pinnacleOdds))
                .build();
        
        BigDecimal expectedMargin = BigDecimal.valueOf(0.05);
        BigDecimal margin = TradeUtil.calculateMargin(trade);
        
        assertTrue("Margin was incorrect, got " + margin, round(margin).compareTo(expectedMargin) == 0);
    }

}
