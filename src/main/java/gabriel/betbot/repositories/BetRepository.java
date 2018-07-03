
package gabriel.betbot.repositories;

import com.google.common.collect.ImmutableList;
import gabriel.betbot.db.Database;
import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.BetStatus;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.jongo.MongoCollection;

/**
 *
 * @author gabriel
 */
@Named
public class BetRepository {
    
    private final MongoCollection bets;
    
    @Inject
    public BetRepository(final Database database) {
        bets = database.getDb().getCollection("bets");
        bets.ensureIndex("{matchId: 1}", "{unique: false, sparse: true}");
    }
    
    public void save(final Bet bet) {
        bets.save(bet);
    }
    
    public Bet saveAndGet(final Bet bet) {
        bets.save(bet);
        return bet;
    }
    
    public List<Bet> findByMatchIdAndStatus(final long matchId, final BetStatus status) {
        return ImmutableList.copyOf(bets.find("{matchId: #, status: #}", matchId, status).as(Bet.class).iterator());
    }

}
