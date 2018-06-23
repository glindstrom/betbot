package gabriel.betbot.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabriel.betbot.utils.Client;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 *
 * @author gabriel
 */
public class AsianOddsClient {

    private static final String WEB_API_USERNAME = System.getenv("ASIANODDSWEBAPIUSERNAME");
    private static final String WEB_API_PASSWORD = System.getenv("ASIANODDSWEBAPIPASSWORD");
    private static final String BASE_URL = "https://webapi700.asianodds88.com/AsianOddsService";
    
    private final Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AsianOddsClient(final Client client) {
        this.client = client;
    }
    
    public String login() {
        String url = BASE_URL + "/Login?username=" + WEB_API_USERNAME + "&password=" + WEB_API_PASSWORD;
        CloseableHttpResponse response = client.doGet(url);
        try {
            return objectMapper.readValue(response.getEntity().getContent(), LoginResponse.class).result.token;
        } catch (IOException ex) {
            Logger.getLogger(AsianOddsClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
    
    public static void main(String[] args) {
        Client client = new Client();
        AsianOddsClient loginService = new AsianOddsClient(client);
        System.out.println(loginService.login());
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class LoginResponse {
        public final Result result;

        @JsonCreator
        public LoginResponse(@JsonProperty("Result") final Result result) {
            this.result = result;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Result {
        public final String token;

        @JsonCreator
        public Result(@JsonProperty("Token") final String token) {
            this.token = token;
        }
        
        
    }
}
