
package gabriel.betbot.repositories;

import com.google.common.collect.ImmutableList;
import gabriel.betbot.db.Database;
import gabriel.betbot.trades.Bet;
import java.util.List;
import javax.inject.Named;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

/**
 *
 * @author gabriel
 */
@Named
public class BetRepository {
    
    private final MongoCollection bets;
    
    public BetRepository(final Database database) {
        bets = database.getDb().getCollection("bets");
        bets.ensureIndex("{gameId: 1}", "{unique: false, sparse: true}");
    }
    
    public void save(final Bet bet) {
        bets.save(bet);
    }
    
    public List<Bet> findByGameId(final long gameId) {
        return ImmutableList.copyOf(bets.find("{gameId: #}", gameId).as(Bet.class).iterator());
    }

}
