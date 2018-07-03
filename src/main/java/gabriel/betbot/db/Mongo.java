
package gabriel.betbot.db;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import javax.inject.Named;
import org.jongo.Jongo;
import org.jongo.marshall.jackson.JacksonMapper;

/**
 *
 * @author gabriel
 */
@Named
public class Mongo implements Database {
    private final Jongo jongo;

    public Mongo() {
        DB db = new MongoClient().getDB("betbot");
        jongo = new Jongo(db, new JacksonMapper.Builder()
                .registerModule(new JavaTimeModule())
                .build());
    }

    @Override
    public Jongo getDb() {
        return jongo;
    }
    
}
