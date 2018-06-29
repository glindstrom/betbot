
package gabriel.betbot.db;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;

/**
 *
 * @author gabriel
 */
public class Mongo implements Database {
    private final Jongo jongo;

    public Mongo() {
        DB db = new MongoClient().getDB("betbot");
        jongo = new Jongo(db);
    }

    @Override
    public Jongo getDb() {
        return jongo;
    }
    
}
