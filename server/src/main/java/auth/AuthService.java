package auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class AuthService {

    public static String AUTH_FILE = "server/src/main/resources/sfiles/auth.db";

    private String login;
    private String pass;

    private static AuthService instance;

    private AuthService() {
    }

    public static AuthService getInstance(){
        if (instance==null) {
            instance = new AuthService();
        }
        return instance;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public String getId(String login, String pass) throws IOException {
        Path path = Paths.get(AUTH_FILE);

        Optional<String> lines = Files.lines(path)
        .filter((str)->{
            String[] line = str.split(" ");
            return login.equals(line[1]) && pass.equals(line[2]);
        })
        .findFirst();
        String userIdStr=null;
        if (lines.isPresent()) {
            userIdStr = (lines.get().split(" "))[0];
        }
        return userIdStr;
    }
}
