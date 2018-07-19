package gabriel.betbot.tools;

import gabriel.betbot.db.MongoDataSource;
import gabriel.betbot.repositories.BetRepository;
import gabriel.betbot.service.AsianOddsClient;
import gabriel.betbot.service.TradeService;
import gabriel.betbot.trades.AsianOddsBetResultUpdater;
import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.BetStatus;
import gabriel.betbot.utils.BetUtil;
import gabriel.betbot.utils.Client;
import java.math.BigDecimal;
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

    public void run() {
        BetRepository betRepo = new BetRepository(new MongoDataSource());
        List<Bet> bets = betRepo.findAll().stream()
                .filter(bet -> bet.getStatus() == BetStatus.SETTLED)
                .filter(bet-> BetUtil.edgeIsGreaterThanOrEqualTo(bet.getEdge(), BigDecimal.valueOf(0.011)))
                .filter(bet -> ChronoUnit.MINUTES.between(bet.getCreated(), bet.getStartTime()) > 120)
                .filter(bet -> ChronoUnit.MINUTES.between(bet.getCreated(), bet.getStartTime()) < 240)
                .collect(Collectors.toList());
        BigDecimal profit = bets.stream()
                .map(Bet::getPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
       LOG.info("Number of bets: {}, profit: {}", bets.size(), profit);

    }

    public static void main(String[] args) {
        Analytics analytics = new Analytics();
        analytics.run();
    }

}
