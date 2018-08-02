package gabriel.betbot.tools;

import gabriel.betbot.db.MongoDataSource;
import gabriel.betbot.repositories.BetRepository;
import gabriel.betbot.service.TradeService;
import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.BetStatus;
import gabriel.betbot.trades.OddsType;
import gabriel.betbot.utils.BetUtil;
import gabriel.betbot.utils.MathUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gabriel
 */
public class Analytics {

    private static final Logger LOG = LogManager.getLogger(Analytics.class.getName());

    private final BetRepository betRepository;

    public Analytics() {
        this.betRepository = new BetRepository(new MongoDataSource());
    }

    public void run() {
        printReportWithExpectedProfit();
        //printReport();

    }

    private void printReport() {
        List<Bet> bets = betRepository.findAll().stream()
                .filter(bet -> bet.getStatus() == BetStatus.SETTLED)
                //.filter(bet -> BetUtil.edgeIsGreaterThanOrEqualTo(bet.getEdge(), BigDecimal.valueOf(0.05)))
                //.filter(bet -> ChronoUnit.MINUTES.between(bet.getCreated(), bet.getStartTime()) > 120)
                .filter(bet -> ChronoUnit.MINUTES.between(bet.getCreated(), bet.getStartTime()) < 120)
                .collect(Collectors.toList());

        BigDecimal profit = bets.stream()
                .map(Bet::getPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LOG.info("Number of bets: {}, profit: {}", bets.size(), profit);
    }

    private void printReportWithExpectedProfit() {
        List<Bet> bets = betRepository.findAll().stream()
                .filter(bet -> bet.getStatus() == BetStatus.SETTLED)
                .filter(bet -> bet.getPinnacleMaximumAmount() > 0)
                .filter(bet -> bet.getTrueClosingOdds() != null)
               // .filter(bet -> bet.getPinnacleMargin() != null)
                .filter(bet -> bet.getOddsType() == OddsType.ONE_X_TWO)
                //.filter(bet -> BetUtil.edgeIsGreaterThan(bet.getEdge(), BigDecimal.valueOf(0.011)))
               // .filter(bet -> BetUtil.edgeIsLessThanOrEqualTo(bet.getEdge(), BigDecimal.valueOf(0.023)))
                //.filter(bet -> ChronoUnit.MINUTES.between(bet.getCreated(), bet.getStartTime()) >= 30)
               // .filter(bet -> ChronoUnit.MINUTES.between(bet.getCreated(), bet.getStartTime()) <= 120)
                .collect(Collectors.toList());

        List<Bet> todaysBets = bets.stream()
                .filter(b -> b.getStartTime().toLocalDate().isEqual(LocalDate.now()))
                .collect(Collectors.toList());
        BigDecimal todaysProfit = BetUtil.calculateProfit(todaysBets);
        LOG.info("Number of bets today: {}, profit: {}", todaysBets.size(), todaysProfit);
        todaysBets.stream()
                .sorted((b1, b2) -> BetUtil.closingEdge(b1).compareTo(BetUtil.closingEdge(b2)))
                .forEach(bet -> LOG.info("Bet: {}, expected edge: {}, closing edge: {} %, {} {}", bet.getId(), MathUtil.round(bet.getEdge()).multiply(BigDecimal.valueOf(100)), MathUtil.round(BetUtil.closingEdge(bet).multiply(BigDecimal.valueOf(100))), bet.getOddsType(), bet.getBetDescription() != null ? bet.getBetDescription() : ""));

        BigDecimal expectedProfit = bets.stream()
                .map(bet -> BetUtil.expectedProfit(bet))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmountWagered = bets.stream()
                .map(Bet::getActualStake)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expectedROI = expectedProfit.divide(totalAmountWagered, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        BigDecimal profit = BetUtil.calculateProfit(bets);

        BigDecimal actualROI = profit.divide(totalAmountWagered, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        BigDecimal expectedFlatProfit = bets.stream()
                .map(bet -> BetUtil.closingEdge(bet))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LOG.info("Expected flat profit: {}", expectedFlatProfit);
        BigDecimal expectedFlatROI = expectedFlatProfit.divide(BigDecimal.valueOf(bets.size()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        BigDecimal flatProfit = bets.stream()
                .map(bet -> BetUtil.unitProfit(bet))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal flatROI = flatProfit.divide(BigDecimal.valueOf(bets.size()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        BigDecimal averageROIwhenBetsWerePlaced = BetUtil.calculateAverageExpectedROIwhenBetIsPlaced(bets).multiply(BigDecimal.valueOf(100));

        int numberOfBetsWithPositiveEdge = bets.stream()
                .map(bet -> BetUtil.closingEdge(bet))
                .filter(edge -> edge.compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList()).size();

        BigDecimal shareOfBetsWithPositiveEdge = MathUtil.divideXbyY(BigDecimal.valueOf(numberOfBetsWithPositiveEdge), BigDecimal.valueOf(bets.size()));

        List<Bet> betsWithShorteningOdds
                = bets.stream()
                .filter(b -> b.getPinnacleOdds().compareTo(b.getClosingOdds()) > 0)
                .collect(Collectors.toList());

        int numberOfEdgeShortening = betsWithShorteningOdds.size();

        int numberOfEdgeLengthening = bets.stream()
                .filter(b -> b.getPinnacleOdds().compareTo(b.getClosingOdds()) < 0)
                .collect(Collectors.toList()).size();

        LOG.info("Number of bets: {}, positive edge: {} %", bets.size(), shareOfBetsWithPositiveEdge.multiply(BigDecimal.valueOf(100)));
        LOG.info("Profit: {}, expected profit: {}", profit, expectedProfit);
        LOG.info("ROI: {} %, expected ROI: {} %", actualROI, expectedROI);
        LOG.info("Flat ROI: {} %, expected flat ROI: {} %, expected ROI when bet was placed: {} %", flatROI, expectedFlatROI, averageROIwhenBetsWerePlaced);
        LOG.info("Number of odds shortening: {}, number of odds lengthenening: {}", numberOfEdgeShortening, numberOfEdgeLengthening);
    }

    public static void main(String[] args) {
        Analytics analytics = new Analytics();
        analytics.run();
    }

}
