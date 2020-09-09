package authservice;

import entities.User;
import network.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class AuthorisationService {

    private enum Stage {
        WAITING_FOR_LOGIN_LENGTH, WAITING_FOR_LOGIN, WAITING_FOR_PASSWORD_LENGTH, WAITING_FOR_PASSWORD, AUTHORIZATION_PROCESS, AUTHORIZATION_SUCCESS, AUTHORIZATION_FAIL
    }

    private Logger logger = LogManager.getLogger(AuthorisationService.class);

    private Stage stage;

    private Network network;

    private User user;
    int loginLength = 0, passwordLength = 0;
    String login = null, password = null;


    public AuthorisationService(Network network) {
        this.network = network;
        this.user = new User (-1, "", ""); // not authorized user
        this.stage = Stage.WAITING_FOR_LOGIN_LENGTH;
    }


    public User getUserByLoginAndPasswordFromClient() {
        while (true) {
            switch (stage) {
                case WAITING_FOR_LOGIN_LENGTH:
                    loginLength = network.readByteFromClient(); // max 127
                    LogUtils.info("Login length " + loginLength + " was received", logger);
                    stage = Stage.WAITING_FOR_LOGIN;
                    break;
                case WAITING_FOR_LOGIN:
                    login = new String(network.readBytesFromClient(loginLength));
                    LogUtils.info("Login " + login + " was received", logger);
                    stage = Stage.WAITING_FOR_PASSWORD_LENGTH;
                    break;
                case WAITING_FOR_PASSWORD_LENGTH:
                    passwordLength = network.readByteFromClient(); // max 127
                    LogUtils.info("Password length " + passwordLength + " was received", logger);
                    stage = Stage.WAITING_FOR_PASSWORD;
                    break;
                case WAITING_FOR_PASSWORD:
                    password = new String(network.readBytesFromClient(passwordLength));
                    LogUtils.info("Password " + password + " was received", logger);
                    stage = Stage.AUTHORIZATION_PROCESS;
                    break;
                case AUTHORIZATION_PROCESS:
                    user = getUserByLoginAndPassword(login, password);
                    if (user.getId() != -1) {
                        stage = Stage.AUTHORIZATION_SUCCESS;
                    } else {
                        stage = Stage.AUTHORIZATION_FAIL;
                    }
                    break;
                case AUTHORIZATION_FAIL:
                    LogUtils.info("User " + login + " was not authorised", logger);
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    return user;
                case AUTHORIZATION_SUCCESS:
                    LogUtils.info("User " + login + " authorised successfully", logger);
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    return user;
            }
        }
    }

    private User getUserByLoginAndPassword(String login, String password) {
        try {
            Optional<String[]> userStringArray = Files.lines(UsersService.AUTH_FILE_PATH)
            .map(s -> s.split(" "))
            .filter(s -> s[1].equals(login) && s[2].equals(password))
            .findFirst();
            if (userStringArray.isPresent()){
                int id = Integer.parseInt(userStringArray.get()[0]);
                String l = userStringArray.get()[1];
                String p = userStringArray.get()[2];
                return new User(id, l, p);
            } else {
                return new User (-1, "", "");
            }
        } catch (IOException e) {
            LogUtils.error("Authorization file can not be readed", logger);
        }
        return new User (-1, "", "");
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

//    private String getIdByNickName(String login) throws IOException {
//        Optional<String> lines = Files.lines(AuthService.AUTH_FILE_PATH)
//                .filter((str) -> {
//                    String[] line = str.split(" ");
//                    return login.equals(line[1]);
//                })
//                .findFirst();
//        String userIdStr = null;
//        if (lines.isPresent()) {
//            userIdStr = (lines.get().split(" "))[0];
//        }
//        return userIdStr;
//    }
//
//    public String getId(String login, String pass) throws IOException {
//        Optional<String> lines = Files.lines(AuthService.AUTH_FILE_PATH)
//                .filter((str) -> {
//                    String[] line = str.split(" ");
//                    return login.equals(line[1]) && pass.equals(line[2]);
//                })
//                .findFirst();
//        String userIdStr = null;
//        if (lines.isPresent()) {
//            userIdStr = (lines.get().split(" "))[0];
//        }
//        return userIdStr;
//    }

}
