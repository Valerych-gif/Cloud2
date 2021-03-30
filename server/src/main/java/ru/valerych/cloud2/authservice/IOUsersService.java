package ru.valerych.cloud2.authservice;

import ru.valerych.cloud2.authservice.interfaces.UsersService;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.UserCantBeAuthorized;
import ru.valerych.cloud2.exceptions.UserNotFoundException;
import ru.valerych.cloud2.network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

import java.nio.file.Path;
import java.nio.file.Paths;

public class IOUsersService implements UsersService {

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

    public static final Path AUTH_FILE_PATH = Paths.get(
            Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
            Cloud2ServerSettings.AUTH_FILE
    );

    private final IOAuthorisationService authorisationService;
    private final IORegistrationService registrationService;
    private final Network network;

    private Stage stage;
    private Mode mode;

    private final Logger logger = LogManager.getLogger(IOUsersService.class);

    public IOUsersService(Network network) {
        this.network = network;
        this.authorisationService = new IOAuthorisationService();
        this.registrationService = new IORegistrationService();
        this.mode = Mode.WAITING;
        this.stage = Stage.WAITING_FOR_LOGIN_LENGTH;
        logger.info("Authorization service started successfully");
    }

    @Override
    public User authUserByLoginAndPassword() {
        mode = Mode.AUTHORIZATION;
        return getUserByLoginAndPasswordFromClient();
    }

    @Override
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
                    logger.info("Login length '" + loginLength + "' was received");
                    stage = Stage.WAITING_FOR_LOGIN;
                    break;
                case WAITING_FOR_LOGIN:
                    login = new String(network.readBytesFromClient(loginLength));
                    logger.info("Login '" + login + "' was received", logger);
                    stage = Stage.WAITING_FOR_PASSWORD_LENGTH;
                    break;
                case WAITING_FOR_PASSWORD_LENGTH:
                    passwordLength = network.readByteFromClient(); // max 127
                    logger.info("Password length was received");
                    stage = Stage.WAITING_FOR_PASSWORD;
                    break;
                case WAITING_FOR_PASSWORD:
                    password = new String(network.readBytesFromClient(passwordLength));
                    logger.info("Password '" + password + "' was received");
                    if (mode == Mode.AUTHORIZATION)
                        stage = Stage.AUTHORIZATION_PROCESS;
                    if (mode == Mode.REGISTRATION)
                        stage = Stage.REGISTRATION_PROCESS;
                    break;

                case AUTHORIZATION_PROCESS:
                    try {
                        user = authorisationService.getUserByLoginAndPassword(login, password);
                    } catch (UserNotFoundException e) {
                        logger.error(e);
                    } catch (UserCantBeAuthorized e){
                        logger.warn(e);
                    }
                    if (user != User.UNAUTHORIZED_USER) {
                        user.setUpUser();
                        stage = Stage.AUTHORIZATION_SUCCESS;
                    } else {
                        stage = Stage.AUTHORIZATION_FAIL;
                    }
                    break;
                case AUTHORIZATION_SUCCESS:
                    logger.info("User " + login + " authorised successfully");
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    mode = Mode.WAITING;
                    return user;
                case AUTHORIZATION_FAIL:
                    logger.info("User '" + login + "' was not authorised");
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    mode = Mode.WAITING;
                    return User.UNAUTHORIZED_USER;

                case REGISTRATION_PROCESS:
                    user = registrationService.getNewUserByLoginAndPassword(login, password);
                    if (user != User.UNAUTHORIZED_USER) {
                        user.setUpUser();
                        stage = Stage.REGISTRATION_SUCCESS;
                    } else {
                        stage = Stage.REGISTRATION_FAIL;
                    }
                    break;
                case REGISTRATION_SUCCESS:
                    logger.info("New user " + login + " registered successfully");
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    mode = Mode.WAITING;
                    return user;
                case REGISTRATION_FAIL:
                    logger.info("New user '" + login + "' was not registered");
                    stage = Stage.WAITING_FOR_LOGIN_LENGTH;
                    mode = Mode.WAITING;
                    return User.UNAUTHORIZED_USER;
            }
        }
    }

}
