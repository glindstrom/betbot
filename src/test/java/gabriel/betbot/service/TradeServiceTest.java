package gabriel.betbot.service;

import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author gabriel
 */
public class TradeServiceTest {
    
    @Test
    public void edgeIsCorrect() {
        BigDecimal offeredOdds = BigDecimal.valueOf(1.95);
        BigDecimal trueOdds = BigDecimal.valueOf(1.901);
        BigDecimal expectedEdge = BigDecimal.valueOf(0.0258);
        
        BigDecimal calculatedEdge = TradeService.calculateEdge(offeredOdds, trueOdds).setScale(4, BigDecimal.ROUND_HALF_UP);
        assertTrue("Edge was incorrect, got " + calculatedEdge,expectedEdge.compareTo(calculatedEdge) == 0);
    } 

}
