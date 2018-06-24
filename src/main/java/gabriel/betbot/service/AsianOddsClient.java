package gabriel.betbot.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import gabriel.betbot.dtos.AccountSummary;
import gabriel.betbot.utils.Client;
import gabriel.betbot.utils.JsonMapper;
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
    private static final String ACCOUNT_SUMMARY_URL = BASE_URL + "/GetAccountSummary";
    
    private final Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Header tokenHeader;

    public AsianOddsClient(final Client client) {
        this.client = client;
    }
    
    public LoginResponse login() {
        CloseableHttpResponse response = client.doGet(LOGIN_URL);
        return JsonMapper.jsonToObject(response, LoginResponse.class);    
    }
    
    public void register(final LoginResponse loginResponse) {
        String registerUrl = loginResponse.result.url + REGISTER_URL_SUFFIX;
        this.tokenHeader = new BasicHeader(TOKEN_HEADER_NAME, loginResponse.result.token);
        Header keyHeader = new BasicHeader(KEY_HEADER_NAME, loginResponse.result.key);
        List<Header> headers = ImmutableList.of(tokenHeader, keyHeader);
        CloseableHttpResponse response = client.doGet(registerUrl, headers);
        try {
            System.out.println(response.getEntity().getContent().toString());
        } catch (IOException | UnsupportedOperationException ex) {
            Logger.getLogger(AsianOddsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loginAndRegister() {
        this.register(this.login());
    }
    
    public AccountSummary getAccountSummary() {
        List<Header> headers = ImmutableList.of(tokenHeader);
        CloseableHttpResponse response = client.doGet(ACCOUNT_SUMMARY_URL, headers);
        return JsonMapper.jsonToObject(response, AccountSummary.class);
    }
    
    public static void main(String[] args) {
        Client client = new Client();
        AsianOddsClient loginService = new AsianOddsClient(client);
        loginService.loginAndRegister();
        System.out.println(loginService.getAccountSummary());
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
