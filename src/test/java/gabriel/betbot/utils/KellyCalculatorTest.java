
package gabriel.betbot.utils;

import java.math.BigDecimal;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author gabriel
 */
public class KellyCalculatorTest {
    
    @Test
    public void calculatesFractionCorrectly() {
        BigDecimal trueOdds = BigDecimal.valueOf(2.0232228442);
        BigDecimal odds = BigDecimal.valueOf(2.09);
        BigDecimal expectedFraction =  BigDecimal.valueOf(0.03).multiply(KellyCalculator.SCALE_FACTOR);
        
        BigDecimal calculatedFraction = KellyCalculator.getKellyFraction(odds, trueOdds).setScale(3, BigDecimal.ROUND_HALF_UP);
        assertTrue("Wrong fraction, got " + calculatedFraction + " expected: " + expectedFraction, expectedFraction.compareTo(calculatedFraction) == 0);
        
    }

}
