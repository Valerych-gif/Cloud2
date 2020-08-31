package authservice;

import connectionhandlers.IOConnectionHandler;
import entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Optional;

public class RegistrationService {

    private Logger logger = LogManager.getLogger(RegistrationService.class);

//    public void registration() throws IOException {
//        getLoginAndPassFromClient();
//        if (login!=null) {
//            try {
//                String userIdStr = authService.registration(login, pass);
//                this.userId = userIdStr != null ? Integer.parseInt(userIdStr) : -1;
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.error(e);
//            }
//        }
//    }

//    public String registration(String login, String pass) throws Exception {
//        String userIdStr = getId(login, pass);
//        if (userIdStr == null) {
//            userIdStr = getNewUserId();
//            if (isLoginFree(login)) {
//                writeNewUserIntoDB(userIdStr, login, pass);
//            } else {
//                return null;
//            }
//        }
//        return userIdStr;
//    }

    synchronized private boolean isLoginFree(String login) throws IOException {
        Optional<String[]> lines = Files.lines(AuthService.AUTH_FILE_PATH)
                .map((str) -> str.split(" "))
                .filter((strings) -> login.equals(strings[1]))
                .findFirst();
        return !lines.isPresent();
    }

    synchronized private String getNewUserId() throws IOException {
        Optional<String[]> lines = Files.lines(AuthService.AUTH_FILE_PATH)
                .map((str) -> str.split(" "))
                .max(Comparator.comparingInt(str -> Integer.parseInt(str[0])));
        if (lines.isPresent()) {
            int newUserId = Integer.parseInt((lines.get())[0]) + 1;
            return String.valueOf(newUserId);
        } else {
            return "0";
        }
    }

    synchronized public void writeNewUserIntoDB(User user) throws Exception {
        String newUserStr = user.getId() + " " + user.getLogin() + " " + user.getPassword() + "\r\n";
        Files.write(AuthService.AUTH_FILE_PATH, newUserStr.getBytes(), StandardOpenOption.APPEND);
    }

    public User createNewUserByLoginAndPassFromClient() {
        return null;
    }
}
