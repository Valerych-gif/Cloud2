package ru.valerych.cloud2.authservice;

import ru.valerych.cloud2.authservice.interfaces.UsersService;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.LoginIsNotFreeException;
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
        WAITING_FOR_PASSWORD
    }

    public static final Path AUTH_FILE_PATH = Paths.get(
            Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
            Cloud2ServerSettings.AUTH_FILE
    );

    private final IOAuthorisationService authorisationService;
    private final IORegistrationService registrationService;
    private final Network network;

    private Stage stage;
    private String login;
    private String password;

    private final Logger logger = LogManager.getLogger(IOUsersService.class.getName());

    public IOUsersService(Network network) {
        this.network = network;
        this.authorisationService = new IOAuthorisationService();
        this.registrationService = new IORegistrationService();
        this.stage = Stage.WAITING_FOR_LOGIN_LENGTH;
        logger.info("Authorization service started successfully");
    }

    @Override
    public User authUserByLoginAndPassword() throws UserNotFoundException, UserCantBeAuthorized {
        getUserByLoginAndPasswordFromClient();
        User user = authorisationService.getUserByLoginAndPassword(login, password);
        user.setUpUser();
        return user;
    }

    @Override
    public User registrationUserByLoginAndPassword() throws LoginIsNotFreeException {
        getUserByLoginAndPasswordFromClient();
        User user = registrationService.getNewUserByLoginAndPassword(login, password);
        user.setUpUser();
        return user;
    }

    private void getUserByLoginAndPasswordFromClient() {

        stage = Stage.WAITING_FOR_LOGIN_LENGTH;
        int loginLength = 0;
        int passwordLength = 0;
        login = null;
        password = null;

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
                    logger.info("Password '******' was received");
                    return;
            }
        }
    }
}
