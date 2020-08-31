package authservice;

import entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class AuthorisationService {

    private Logger logger = LogManager.getLogger(AuthorisationService.class);

    User user;

    public AuthorisationService() {
        this.user = null;
    }

//    public void authorization() throws IOException {
//        getLoginAndPassFromClient();
//        if (user!=null) {
//            try {
//                String userIdStr = getId(login, pass);
//                this.userId = userIdStr != null ? Integer.parseInt(userIdStr) : -1;
//            } catch (IOException e) {
//                e.printStackTrace();
//                logger.error(e);
//            }
//        }
//    }
//
//    public void getLoginAndPassFromClient() throws IOException {
//        String[] loginAndPassStr = getStringFromClient().split(" ");
//        if (loginAndPassStr.length > 1) {
//            this.login = loginAndPassStr[0].trim();
//            this.pass = loginAndPassStr[1].trim();
//        } else {
//            this.login = null;
//            this.pass = null;
//        }
//    }

    private String getIdByNickName(String login) throws IOException {
        Optional<String> lines = Files.lines(AuthService.AUTH_FILE_PATH)
                .filter((str) -> {
                    String[] line = str.split(" ");
                    return login.equals(line[1]);
                })
                .findFirst();
        String userIdStr = null;
        if (lines.isPresent()) {
            userIdStr = (lines.get().split(" "))[0];
        }
        return userIdStr;
    }

    public String getId(String login, String pass) throws IOException {
        Optional<String> lines = Files.lines(AuthService.AUTH_FILE_PATH)
                .filter((str) -> {
                    String[] line = str.split(" ");
                    return login.equals(line[1]) && pass.equals(line[2]);
                })
                .findFirst();
        String userIdStr = null;
        if (lines.isPresent()) {
            userIdStr = (lines.get().split(" "))[0];
        }
        return userIdStr;
    }

    public User getUserByLoginAndPassFromClient() {
        return null;
    }
}
