package authservice;

import entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Optional;

public class RegistrationService {

    private Logger logger = LogManager.getLogger(RegistrationService.class);

    public RegistrationService() {
    }

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
        Optional<String[]> lines = null;
        try {
            lines = Files.lines(UsersService.AUTH_FILE_PATH)
                    .map((str) -> str.split(" "))
                    .filter((strings) -> login.equals(strings[1]))
                    .findFirst();
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
        return !lines.isPresent();
    }

    synchronized private int getNewUserId(){
        Optional<String[]> lines = null;
        try {
            lines = Files.lines(UsersService.AUTH_FILE_PATH)
                    .map((str) -> str.split(" "))
                    .max(Comparator.comparingInt(str -> Integer.parseInt(str[0])));
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
        return lines.map(strings -> Integer.parseInt((strings)[0]) + 1).orElse(-1);
    }

    synchronized public void writeNewUserIntoDB(User user){
        String newUserStr = user.getId() + " " + user.getLogin() + " " + user.getPassword() + "\r\n";
        try {
            Files.write(UsersService.AUTH_FILE_PATH, newUserStr.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
    }
}
