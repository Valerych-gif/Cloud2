package authservice;

import entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class AuthorisationService {

    private Logger logger = LogManager.getLogger(AuthorisationService.class);

    public AuthorisationService() {
    }

    public User getUserByLoginAndPassword(String login, String password) {
        try {
            Optional<String[]> userStringArray = Files.lines(UsersService.AUTH_FILE_PATH)
            .map(s -> s.split(" "))
            .filter(s -> s[1].equals(login) && s[2].equals(password))
            .findFirst();
            if (userStringArray.isPresent()){
                int id = Integer.parseInt(userStringArray.get()[0]);
                String l = userStringArray.get()[1];
                String p = userStringArray.get()[2];
                return User.UNAUTHORIZED_USER;
            } else {
                return User.UNAUTHORIZED_USER;
            }
        } catch (IOException e) {
            LogUtils.error("Authorization file can not be read", logger);
        }
        return User.UNAUTHORIZED_USER;
    }
}
