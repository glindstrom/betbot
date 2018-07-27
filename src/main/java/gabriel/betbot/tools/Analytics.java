package gabriel.betbot.tools;

import gabriel.betbot.db.MongoDataSource;
import gabriel.betbot.repositories.BetRepository;
import gabriel.betbot.service.TradeService;
import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.BetStatus;
import gabriel.betbot.utils.BetUtil;
import gabriel.betbot.utils.MathUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
                //.filter(bet -> BetUtil.edgeIsGreaterThan(bet.getEdge(), BigDecimal.valueOf(0.02)))
                //filter(bet -> BetUtil.edgeIsLessThanOrEqualTo(bet.getEdge(), BigDecimal.valueOf(0.05)))
                //.filter(bet -> ChronoUnit.MINUTES.between(bet.getCreated(), bet.getStartTime()) >= 30)
                // .filter(bet -> ChronoUnit.MINUTES.between(bet.getCreated(), bet.getStartTime()) <= 30)
                .collect(Collectors.toList());

        bets.stream()
                .sorted((b1, b2) -> BetUtil.expectedProfit(b1).compareTo(BetUtil.expectedProfit(b2)))
                .forEach(bet -> LOG.info("Bet: {}, closing edge: {} %, expected profit: {}", bet.getId(), MathUtil.round(BetUtil.closingEdge(bet).multiply(BigDecimal.valueOf(100))), BetUtil.expectedProfit(bet)));

        BigDecimal expectedProfit = bets.stream()
                .map(bet -> BetUtil.expectedProfit(bet))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmountWagered = bets.stream()
                .map(Bet::getActualStake)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expectedROI = expectedProfit.divide(totalAmountWagered, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        BigDecimal profit = bets.stream()
                .map(Bet::getPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal actualROI = profit.divide(totalAmountWagered, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        BigDecimal flatProfit = bets.stream()
                .map(bet -> BetUtil.closingEdge(bet))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LOG.info("flat profit: {}", flatProfit);
        BigDecimal expectedFlatROI = flatProfit.divide(BigDecimal.valueOf(bets.size()), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        LOG.info("Number of bets: {}, profit: {}, expected profit: {}, ROI: {} %, expected ROI: {} %, expected flat ROI: {} %", bets.size(), profit, expectedProfit, actualROI, expectedROI, expectedFlatROI);
    }

    public static void main(String[] args) {
        Analytics analytics = new Analytics();
        analytics.run();
    }

}
