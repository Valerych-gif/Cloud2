package ru.valerych.cloud2.authservice;

import ru.valerych.cloud2.authservice.interfaces.RegistrationService;
import ru.valerych.cloud2.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Optional;

public class IORegistrationService implements RegistrationService {

    private final Logger logger = LogManager.getLogger(IORegistrationService.class);

    public IORegistrationService() {
    }

    @Override
    public User getNewUserByLoginAndPassword(String login, String password){
        if (!isLoginFree(login))
            return User.UNAUTHORIZED_USER;

        int userId = getNewUserId();
        if (userId == -1)
            return User.UNAUTHORIZED_USER;

        User newUser = new User (userId, login, password);
        writeNewUserIntoDB(newUser);
        return newUser;
    }

    synchronized private boolean isLoginFree(String login) {
        Optional<String[]> lines = Optional.empty();
        try {
            lines = Files.lines(IOUsersService.AUTH_FILE_PATH)
                    .map((str) -> str.split(" "))
                    .filter((strings) -> login.equals(strings[1]))
                    .findFirst();
        } catch (IOException e) {
            logger.error(e.toString());
        }
        return !lines.isPresent();
    }

    synchronized private int getNewUserId(){
        Optional<String[]> lines = Optional.empty();
        try {
            lines = Files.lines(IOUsersService.AUTH_FILE_PATH)
                    .map((str) -> str.split(" "))
                    .max(Comparator.comparingInt(str -> Integer.parseInt(str[0])));
        } catch (IOException e) {
            logger.error(e.toString());
        }
        return lines.map(strings -> Integer.parseInt((strings)[0]) + 1).orElse(-1);
    }

    synchronized private void writeNewUserIntoDB(User user){
        String newUserStr = user.getId() + " " + user.getLogin() + " " + user.getPassword() + "\r\n";
        try {
            Files.write(IOUsersService.AUTH_FILE_PATH, newUserStr.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }
}
