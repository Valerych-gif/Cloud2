package ru.valerych.cloud2.authservice;

import ru.valerych.cloud2.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.utils.LogUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class AuthorisationService {

    private final Logger logger = LogManager.getLogger(AuthorisationService.class);

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
                return new User(id, login, password);
            } else {
                return User.UNAUTHORIZED_USER;
            }
        } catch (IOException e) {
            LogUtils.error("Authorization file can not be read", logger);
        }
        return User.UNAUTHORIZED_USER;
    }
}
