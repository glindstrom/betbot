package gabriel.betbot.service;

import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsType;
import java.math.BigDecimal;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author gabriel
 */
public class TradeServiceTest {

    @Test
    public void trueOddsAreCorrectFor1X2() {
        Odds offeredOdds = new Odds.Builder()
                .withHomeOdds(BigDecimal.valueOf(1.44))
                .withAwayOdds(BigDecimal.valueOf(6.25))
                .withDrawOdds(BigDecimal.valueOf(4.42))
                .withOddsType(OddsType.ONE_X_TWO)
                .build();

        Odds trueOdds = TradeService.calculateTrueOdds(offeredOdds);
        BigDecimal expectedTrueOdds1 = BigDecimal.valueOf(1.498);
        BigDecimal expectedTrueOdds2 = BigDecimal.valueOf(7.511);
        BigDecimal expectedTrueOddsX = BigDecimal.valueOf(5.016);

        assertTrue("Odds1 were incorrect, got " + trueOdds.getOdds1(), trueOdds.getOdds1().compareTo(expectedTrueOdds1) == 0);
        assertTrue("Odds2 were incorrect, got " + trueOdds.getOdds2(), trueOdds.getOdds2().compareTo(expectedTrueOdds2) == 0);
        assertTrue("OddsX were incorrect, got " + trueOdds.getOddsX(), trueOdds.getOddsX().compareTo(expectedTrueOddsX) == 0);

    }

    @Test
    public void trueOddsAreCorrectForAsianHandicap() {
        Odds offeredOdds = new Odds.Builder()
                .withHomeOdds(BigDecimal.valueOf(1.819))
                .withAwayOdds(BigDecimal.valueOf(2.01))
                .withOddsType(OddsType.HANDICAP)
                .build();

        Odds trueOdds = TradeService.calculateTrueOdds(offeredOdds);
        BigDecimal expectedTrueOdds1 = BigDecimal.valueOf(1.901);

        assertTrue("Odds1 were incorrect, got " + trueOdds.getOdds1(), trueOdds.getOdds1().compareTo(expectedTrueOdds1) == 0);
    }
    
    @Test
    public void edgeIsCorrect() {
        BigDecimal offeredOdds = BigDecimal.valueOf(1.95);
        BigDecimal trueOdds = BigDecimal.valueOf(1.901);
        BigDecimal expectedEdge = BigDecimal.valueOf(0.0258);
        
        BigDecimal calculatedEdge = TradeService.calculateEdge(offeredOdds, trueOdds);
        assertTrue("Edge was incorrect, got " + calculatedEdge,expectedEdge.compareTo(calculatedEdge) == 0);
    } 

}
