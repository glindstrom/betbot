package gabriel.betbot.service;

/**
 *
 * @author gabriel
 */
public class LoginService {

    private static final String WEB_API_USERNAME = System.getenv("ASIANODDSWEBAPIUSERNAME");
    private static final String WEB_API_PASSWORD = System.getenv("ASIANODDSWEBAPIPASSWORD");
    
    public static void main(String[] args) {
        System.out.println(WEB_API_PASSWORD);
        System.out.println(WEB_API_USERNAME);
    }
}
