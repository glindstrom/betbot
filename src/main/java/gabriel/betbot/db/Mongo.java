
package gabriel.betbot.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.text.SimpleDateFormat;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;

/**
 *
 * @author gabriel
 */
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
