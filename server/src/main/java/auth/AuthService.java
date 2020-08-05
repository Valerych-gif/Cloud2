package auth;

public class AuthService {

    private static AuthService instance;

    private AuthService() {
    }

    public static AuthService getInstance(){
        if (instance==null) {
            instance = new AuthService();
        }
        return instance;
    }

    public String getLogin(){
        return "login";
    }

    public String getPassword(){
        return "1234";
    }

    public String getId(){
        return "1";
    }
}
