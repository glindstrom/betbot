package gabriel.betbot.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author gabriel
 */
@Named
public class Client {

    private final CloseableHttpClient client;

    public Client() {
        client = HttpClientBuilder.create().build();
    }

    public CloseableHttpResponse doGet(final String url) {
        HttpGet request = new HttpGet(url);
        return executeRequest(request);
    }

    public CloseableHttpResponse doGet(final String url, final List<Header> headers) {
        HttpGet request = new HttpGet(url);
        headers.forEach(request::addHeader);
        return executeRequest(request);
    }

    public CloseableHttpResponse doPost(final String url, final List<Header> headers, final String json) {
        try {
            HttpPost httpPost = new HttpPost(url);
            headers.forEach(httpPost::addHeader);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return executeRequest(httpPost);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private CloseableHttpResponse executeRequest(final HttpUriRequest request) {
        try {
            return client.execute(request);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

}
