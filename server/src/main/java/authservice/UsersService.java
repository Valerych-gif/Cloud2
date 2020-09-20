package authservice;

import entities.User;
import network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import shareservice.ShareService;
import utils.LogUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class UsersService {

    private enum Stage {
        WAITING_FOR_LOGIN_LENGTH,
        WAITING_FOR_LOGIN,
        WAITING_FOR_PASSWORD_LENGTH,
        WAITING_FOR_PASSWORD,
        AUTHORIZATION_PROCESS,
        REGISTRATION_PROCESS,
        AUTHORIZATION_SUCCESS,
        AUTHORIZATION_FAIL,
        REGISTRATION_SUCCESS,
        REGISTRATION_FAIL
    }

    private enum Mode {
        AUTHORIZATION,
        REGISTRATION,
        WAITING
    }

    public final static Path AUTH_FILE_PATH = Paths.get(
            Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
            Cloud2ServerSettings.AUTH_FILE
    );

    private AuthorisationService authorisationService;
    private RegistrationService registrationService;
    private ShareService shareService;
    private Network network;

    private Stage stage;
    private Mode mode;

    private Logger logger = LogManager.getLogger(UsersService.class);

    public UsersService(Network network) {
        this.network = network;
        this.authorisationService = new AuthorisationService();
        this.registrationService = new RegistrationService();
        this.shareService = new ShareService();
        this.mode = Mode.WAITING;
        this.stage = Stage.WAITING_FOR_LOGIN_LENGTH;
        LogUtils.info("Authorization service started successfully", logger);
    }

    public User authUserByLoginAndPassword() {
        mode = Mode.AUTHORIZATION;
        return getUserByLoginAndPasswordFromClient();
    }

    public User registrationUserByLoginAndPassword() {
        mode = Mode.REGISTRATION;
        return getUserByLoginAndPasswordFromClient();
    }

    private User getUserByLoginAndPasswordFromClient() {

        if (mode == Mode.WAITING) return User.UNAUTHORIZED_USER;
        stage = Stage.WAITING_FOR_LOGIN_LENGTH;

        User user = User.UNAUTHORIZED_USER;
        int loginLength = 0;
        int passwordLength = 0;
        String login = null;
        String password = null;

        while (true) {
            switch (stage) {
                case WAITING_FOR_LOGIN_LENGTH:
                    loginLength = network.readByteFromClient(); // max 127
                    LogUtils.info("Login length '" + loginLength + "' was received", logger);
                    stage = Stage.WAITING_FOR_LOGIN;
                    break;
                case WAITING_FOR_LOGIN:
                    login = new String(network.readBytesFromClient(loginLength));
                    LogUtils.info("Login '" + login + "' was received", logger);
                    stage = Stage.WAITING_FOR_PASSWORD_LENGTH;
                    break;
                case WAITING_FOR_PASSWORD_LENGTH:
                    passwordLength = network.readByteFromClient(); // max 127
                    LogUtils.info("Password length was received", logger);
                    stage = Stage.WAITING_FOR_PASSWORD;
                    break;
                case WAITING_FOR_PASSWORD:
                    password = new String(network.readBytesFromClient(passwordLength));
                    LogUtils.info("Password '" + password + "' was received", logger);
                    if (mode == Mode.AUTHORIZATION)
                        stage = Stage.AUTHORIZATION_PROCESS;
                    if (mode == Mode.REGISTRATION)
                        stage = Stage.REGISTRATION_PROCESS;
                    break;

                case AUTHORIZATION_PROCESS:
                    user = authorisationService.getUserByLoginAndPassword(login, password);
                    if (user != User.UNAUTHORIZED_USER) {
                        stage = Stage.AUTHORIZATION_SUCCESS;
                    } else {
                        stage = Stage.AUTHORIZATION_FAIL;
                    }
                    break;
                case AUTHORIZATION_SUCCESS:
                    LogUtils.info("User " + login + " authorised successfully", logger);
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    mode = Mode.WAITING;
                    return user;
                case AUTHORIZATION_FAIL:
                    LogUtils.info("User '" + login + "' was not authorised", logger);
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    mode = Mode.WAITING;
                    return User.UNAUTHORIZED_USER;

                case REGISTRATION_PROCESS:
                    user = registrationService.getNewUserByLoginAndPassword(login, password);
                    if (user != User.UNAUTHORIZED_USER) {
                        stage = Stage.REGISTRATION_SUCCESS;
                    } else {
                        stage = Stage.REGISTRATION_FAIL;
                    }
                    break;
                case REGISTRATION_SUCCESS:
                    LogUtils.info("New user " + login + " registered successfully", logger);
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    mode = Mode.WAITING;
                    return user;
                case REGISTRATION_FAIL:
                    LogUtils.info("New user '" + login + "' was not registered", logger);
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    mode = Mode.WAITING;
                    return User.UNAUTHORIZED_USER;
            }
        }
    }

    public void shareFile() {
        shareService.shareFileByCommandFromClient();
    }
}
