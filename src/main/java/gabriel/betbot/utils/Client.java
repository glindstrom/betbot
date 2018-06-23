
package gabriel.betbot.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author gabriel
 */
public class Client {
    
    private final CloseableHttpClient client;

    public Client() {
        client = HttpClientBuilder.create().build();
    }
    
    public CloseableHttpResponse doGet(final String url) {
        HttpGet request = new HttpGet(url);
        try {
            return client.execute(request);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

}
