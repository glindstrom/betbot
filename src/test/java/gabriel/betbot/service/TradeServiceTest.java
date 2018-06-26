
package gabriel.betbot.service;

import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsType;
import java.math.BigDecimal;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author gabriel
 */
public class TradeServiceTest {
    
    @Test
    public void testCalcuateTrueOdds() {
        Odds offeredOdds = new Odds.Builder()
                .withHomeOdds(BigDecimal.valueOf(1.44))
                .withAwayOdds(BigDecimal.valueOf(6.25))
                .withDrawOdds(BigDecimal.valueOf(4.42))
                .withOddsType(OddsType.ONE_X_TWO)
                .build();
        
        Odds trueOdds = TradeService.calculateTrueOdds(offeredOdds);
        BigDecimal expectedTrueOdds1 = BigDecimal.valueOf(1.498);
        BigDecimal expectedTrueOdds2 = BigDecimal.valueOf(7.5);
        BigDecimal expectedTrueOddsX = BigDecimal.valueOf(5.011);
        
        assertTrue("Odds1 were incorrect, got " + trueOdds.getOdds1(), trueOdds.getOdds1().compareTo(expectedTrueOdds1) == 0);
        assertTrue("Odds2 were incorrect, got " + trueOdds.getOdds2(), trueOdds.getOdds2().compareTo(expectedTrueOdds2) == 0);
        assertTrue("OddsX were incorrect, got " + trueOdds.getOddsX(), trueOdds.getOddsX().compareTo(expectedTrueOddsX) == 0);

    }

}
