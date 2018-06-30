
package gabriel.betbot.db;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.marshall.jackson.JacksonMapper;

/**
 *
 * @author gabriel
 */
public class Mongo implements Database {
    private final Jongo jongo;

    public Mongo() {
        DB db = new MongoClient().getDB("betbot");
        jongo = new Jongo(db, new JacksonMapper.Builder().registerModule(new JodaModule()).build());
    }

    @Override
    public Jongo getDb() {
        return jongo;
    }
    
}
