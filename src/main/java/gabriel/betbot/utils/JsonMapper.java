package gabriel.betbot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.io.File;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gabriel
 */
public class JsonMapper {

    private static final Logger LOG = LogManager.getLogger(JsonMapper.class.getName());

    public static <T> T jsonToObject(final CloseableHttpResponse response,
            final Class<T> classType) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            return objectMapper.readValue(response.getEntity().getContent(), classType);
        } catch (IOException ex) {
            LOG.error("Error converting object to json", ex);
            throw new RuntimeException(ex);
        }
    }

    public static void writeObjectToFile(final Object object, final String path) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            objectMapper.writeValue(new File(path), object);
        } catch (IOException ex) {
            LOG.error("Error saving object to file", ex);
            throw new RuntimeException(ex);
        }
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        return objectMapper;
    }
}
