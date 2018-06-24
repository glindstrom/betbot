
package gabriel.betbot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 *
 * @author gabriel
 */
public class JsonMapper {
   
    public static <T> T jsonToObject(final CloseableHttpResponse response, 
            final Class<T> classType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(response.getEntity().getContent(), classType);
        } catch (IOException ex) {
            Logger.getLogger(JsonMapper.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

}
