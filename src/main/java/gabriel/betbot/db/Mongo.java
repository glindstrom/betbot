
package gabriel.betbot.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.text.SimpleDateFormat;
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
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(df);
        jongo = new Jongo(db, new JacksonMapper.Builder(mapper)
                .registerModule(new JavaTimeModule())
                .build());
    }

    @Override
    public Jongo getDb() {
        return jongo;
    }
    
}
