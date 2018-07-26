package gabriel.betbot.utils;

import gabriel.betbot.trades.Odds;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.trades.Team;
import java.math.BigDecimal;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author gabriel
 */
public class BetUtilTest {

    @Test
    public void trueOddsAreCorrectFor1X2() {
        Odds offeredOdds = new Odds.Builder()
                .withHomeOdds(BigDecimal.valueOf(1.44))
                .withAwayOdds(BigDecimal.valueOf(6.25))
                .withDrawOdds(BigDecimal.valueOf(4.42))
                .withOddsType(OddsType.ONE_X_TWO)
                .build();

        Odds trueOdds = BetUtil.calculateTrueOdds(offeredOdds);
        BigDecimal expectedTrueOdds1 = BigDecimal.valueOf(1.498);
        BigDecimal expectedTrueOdds2 = BigDecimal.valueOf(7.513);
        BigDecimal expectedTrueOddsX = BigDecimal.valueOf(5.016);

        assertTrue("Odds1 were incorrect, got " + round(trueOdds.getOdds1()), round(trueOdds.getOdds1()).compareTo(expectedTrueOdds1) == 0);
        assertTrue("Odds2 were incorrect, got " + trueOdds.getOdds2(), round(trueOdds.getOdds2()).compareTo(expectedTrueOdds2) == 0);
        assertTrue("OddsX were incorrect, got " + trueOdds.getOddsX(), round(trueOdds.getOddsX()).compareTo(expectedTrueOddsX) == 0);

    }

    public static BigDecimal round(final BigDecimal odds) {
        return odds.setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    @Test
    public void trueOddsAreCorrectForAsianHandicap() {
        Odds offeredOdds = new Odds.Builder()
                .withHomeOdds(BigDecimal.valueOf(1.819))
                .withAwayOdds(BigDecimal.valueOf(2.01))
                .withOddsType(OddsType.HANDICAP)
                .build();

        Odds trueOdds = BetUtil.calculateTrueOdds(offeredOdds);
        BigDecimal expectedTrueOdds1 = BigDecimal.valueOf(1.901);

        assertTrue("Odds1 were incorrect, got " + round(trueOdds.getOdds1()), round(trueOdds.getOdds1()).compareTo(expectedTrueOdds1) == 0);
    }

    @Test
    public void trueOddsAreCorrectForDrawNoBet() {
        Odds true1x2odds = new Odds.Builder()
                .withHomeOdds(BigDecimal.valueOf(1.92191554448079))
                .withAwayOdds(BigDecimal.valueOf(4.6917000397266))
                .withDrawOdds(BigDecimal.valueOf(3.75173399304316))
                .withOddsType(OddsType.HANDICAP)
                .build();

        Odds trueOdds = BetUtil.calcultateTrueDrawNoBetOdds(true1x2odds);
        BigDecimal expectedTrueHomeOdds = BigDecimal.valueOf(1.410);
        BigDecimal expectedTrueAwayOdds = BigDecimal.valueOf(3.441);

        assertTrue("Draw No Bet Home Odds were incorrect, got " + round(trueOdds.getHomeOdds()), round(trueOdds.getHomeOdds()).compareTo(expectedTrueHomeOdds) == 0);
        assertTrue("Draw No Bet Away Odds were incorrect, got " + round(trueOdds.getHomeOdds()), round(trueOdds.getAwayOdds()).compareTo(expectedTrueAwayOdds) == 0);
    }

    @Test
    public void trueOddsAreCorrectForQuarterAsianHandicapBetWhenHomeTeamIsFavored() {
        Odds true1x2odds = new Odds.Builder()
                .withHomeOdds(BigDecimal.valueOf(2.30637974053183))
                .withAwayOdds(BigDecimal.valueOf(3.73570580549706))
                .withDrawOdds(BigDecimal.valueOf(3.34747055710291))
                .withOddsType(OddsType.HANDICAP)
                .build();

        Odds trueOdds = BetUtil.calculateQuarterHandicapTrueOdds(true1x2odds, Team.HOME_TEAM);
        BigDecimal expectedTrueHomeOdds = BigDecimal.valueOf(1.962);
        BigDecimal expectedTrueAwayOdds = BigDecimal.valueOf(2.040);

        assertTrue("Quarter handicap Home Odds were incorrect, got " + round(trueOdds.getHomeOdds()), round(trueOdds.getHomeOdds()).compareTo(expectedTrueHomeOdds) == 0);
        assertTrue("Quarter handicap Away Odds were incorrect, got " + round(trueOdds.getAwayOdds()), round(trueOdds.getAwayOdds()).compareTo(expectedTrueAwayOdds) == 0);

    }
    
    
    @Test
    public void trueOddsAreCorrectForQuarterAsianHandicapBetWhenAwayTeamIsFavored() {
        Odds true1x2odds = new Odds.Builder()
                .withAwayOdds(BigDecimal.valueOf(2.30637974053183))
                .withHomeOdds(BigDecimal.valueOf(3.73570580549706))
                .withDrawOdds(BigDecimal.valueOf(3.34747055710291))
                .withOddsType(OddsType.HANDICAP)
                .build();

        Odds trueOdds = BetUtil.calculateQuarterHandicapTrueOdds(true1x2odds, Team.AWAY_TEAM);
        BigDecimal expectedTrueAwayOdds = BigDecimal.valueOf(1.962);
        BigDecimal expectedTrueHomeOdds = BigDecimal.valueOf(2.040);

        assertTrue("Quarter handicap Home Odds were incorrect, got " + round(trueOdds.getHomeOdds()), round(trueOdds.getHomeOdds()).compareTo(expectedTrueHomeOdds) == 0);
        assertTrue("Quarter handicap Away Odds were incorrect, got " + round(trueOdds.getAwayOdds()), round(trueOdds.getAwayOdds()).compareTo(expectedTrueAwayOdds) == 0);

    }

}
