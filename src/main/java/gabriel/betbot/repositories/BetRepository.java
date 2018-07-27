
package gabriel.betbot.repositories;

import com.google.common.collect.ImmutableList;
import gabriel.betbot.trades.Bet;
import gabriel.betbot.trades.BetStatus;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.jongo.MongoCollection;
import gabriel.betbot.db.DataSource;

/**
 *
 * @author gabriel
 */
@Named
public class BetRepository {
    
    private final MongoCollection bets;
    
    @Inject
    public BetRepository(final DataSource database) {
        bets = database.getDb().getCollection("bets");
        bets.ensureIndex("{matchId: 1}", "{unique: false, sparse: true}");
        bets.ensureIndex("{gameId: 1}", "{unique: false, sparse: true}");
        bets.ensureIndex("{betPlacementReference: 1}", "{unique: true, sparse: true}");
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
    
    public List<Bet> findByGameIdAndStatus(final long gameId, final BetStatus status) {
        return ImmutableList.copyOf(bets.find("{gameId: #, status: #}", gameId, status).as(Bet.class).iterator());
    }
    
    public Bet findByBetPlacementReferece(final String betPlacementReference) {
        return bets.findOne("{betPlacementReference: #}", betPlacementReference).as(Bet.class);
    }
    
    public List<Bet> findAll() {
        return ImmutableList.copyOf(bets.find().as(Bet.class).iterator());
    }

}
