package gabriel.betbot.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import gabriel.betbot.utils.Client;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;

/**
 *
 * @author gabriel
 */
public class AsianOddsClient {

    private static final String WEB_API_USERNAME = System.getenv("ASIANODDSWEBAPIUSERNAME");
    private static final String WEB_API_PASSWORD = System.getenv("ASIANODDSWEBAPIPASSWORD");
    private static final String BASE_URL = "https://webapi700.asianodds88.com/AsianOddsService";
    private static final String LOGIN_URL = BASE_URL + "/Login?username=" + WEB_API_USERNAME + "&password=" + WEB_API_PASSWORD;
    private static final String REGISTER_URL_SUFFIX = "/Register?username=" + WEB_API_USERNAME;
    private static final String TOKEN_HEADER_NAME = "AOToken";
    private static final String KEY_HEADER_NAME = "AOKey";
    
    private final Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AsianOddsClient(final Client client) {
        this.client = client;
    }
    
    public LoginResponse login() {
        CloseableHttpResponse response = client.doGet(LOGIN_URL);
        try {
            return objectMapper.readValue(response.getEntity().getContent(), LoginResponse.class);
        } catch (IOException ex) {
            Logger.getLogger(AsianOddsClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
    
    public void register(final LoginResponse loginResponse) {
        String registerUrl = loginResponse.result.url + REGISTER_URL_SUFFIX;
        Header tokenHeader = new BasicHeader(TOKEN_HEADER_NAME, loginResponse.result.token);
        Header keyHeader = new BasicHeader(KEY_HEADER_NAME, loginResponse.result.key);
        List<Header> headers = ImmutableList.of(tokenHeader, keyHeader);
        CloseableHttpResponse response = client.doGet(registerUrl, headers);
        try {
            System.out.println(response.getEntity().getContent().toString());
        } catch (IOException | UnsupportedOperationException ex) {
            Logger.getLogger(AsianOddsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        Client client = new Client();
        AsianOddsClient loginService = new AsianOddsClient(client);
        loginService.register(loginService.login());
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
        public final String url;
        public final String key;

        @JsonCreator
        public Result(@JsonProperty("Token") final String token, 
                @JsonProperty("Url") final String url, 
                @JsonProperty("Key") final String key) {
            this.token = token;
            this.url = url;
            this.key = key;
        }
        
        
    }
}
