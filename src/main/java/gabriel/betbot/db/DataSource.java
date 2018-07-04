

package gabriel.betbot.db;

import org.jongo.Jongo;


/**
 *
 * @author gabriel
 */
public interface DataSource {
    Jongo getDb();
}
