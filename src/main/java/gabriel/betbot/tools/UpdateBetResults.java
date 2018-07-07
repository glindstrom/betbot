package gabriel.betbot.tools;

import gabriel.betbot.db.MongoDataSource;
import gabriel.betbot.repositories.BetRepository;
import gabriel.betbot.service.AsianOddsClient;
import gabriel.betbot.trades.AsianOddsBetResultUpdater;
import gabriel.betbot.utils.Client;
import java.time.LocalDate;

/**
 *
 * @author gabriel
 */
public class UpdateBetResults {

    public UpdateBetResults() {
    }

    public void run() {
        Client client = new Client();
        AsianOddsClient asianOddsClient = new AsianOddsClient(client);
        BetRepository betRepo = new BetRepository(new MongoDataSource());
        AsianOddsBetResultUpdater updater = new AsianOddsBetResultUpdater(asianOddsClient, betRepo);
        LocalDate date = LocalDate.now();
        updater.updateResults(date);
    }

    public static void main(String[] args) {
        UpdateBetResults ubr = new UpdateBetResults();
        ubr.run();
    }

}
