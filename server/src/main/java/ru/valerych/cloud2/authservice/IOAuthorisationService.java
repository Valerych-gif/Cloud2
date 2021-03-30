package ru.valerych.cloud2.authservice;

import ru.valerych.cloud2.authservice.interfaces.AuthorisationService;
import ru.valerych.cloud2.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.exceptions.UserCantBeAuthorized;
import ru.valerych.cloud2.exceptions.UserNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static ru.valerych.cloud2.authservice.IOUsersService.AUTH_FILE_PATH;

public class IOAuthorisationService implements AuthorisationService {

    private final Logger logger = LogManager.getLogger(IOAuthorisationService.class);

    public IOAuthorisationService() {
    }

    @Override
    public User getUserByLoginAndPassword(String login, String password) throws UserNotFoundException, UserCantBeAuthorized {

        try {
            Optional<String[]> userStringArray = Files.lines(AUTH_FILE_PATH)
                    .map(s -> s.split(" "))
                    .filter(s -> s[1].equals(login))
                    .findFirst();

            String[] userString = userStringArray.orElseThrow(() -> new UserNotFoundException(String.format("User with login '%s' isn't exist", login)));
            if (!userString[2].equals(password)) throw new UserCantBeAuthorized(String.format("There was an unsuccessful attempt to authorize user with login '%s'", login));
            int id = Integer.parseInt(userString[0]);
            return new User(id, login, password);
        } catch (IOException e) {
            logger.error(String.format("Authorization file '%s' can not be read", AUTH_FILE_PATH.toAbsolutePath().toString()));
        }
        throw new UserNotFoundException(String.format("User with login '%s' isn't exist", login));
    }
}
