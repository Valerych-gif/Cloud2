package ru.valerych.cloud2.authservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.authservice.interfaces.AuthorisationService;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.UserCantBeAuthorized;
import ru.valerych.cloud2.exceptions.UserNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;

import static ru.valerych.cloud2.authservice.IOUsersService.AUTH_FILE_PATH;

public class IOAuthorisationService implements AuthorisationService {

    private final Logger logger = LogManager.getLogger(IOAuthorisationService.class.getName());
    private final File authFile;

    public IOAuthorisationService() {
        authFile = AUTH_FILE_PATH.toFile();
    }

    @Override
    public User getUserByLoginAndPassword(String login, String password) throws UserNotFoundException, UserCantBeAuthorized {
        String[] userString = findUserStringByLogin(login);

        if (!userString[2].equals(password))
            throw new UserCantBeAuthorized(String.format("There was an unsuccessful attempt to authorize user with login '%s'", login));
        int id = Integer.parseInt(userString[0]);
        return new User(id, login, password);
    }

    private String[] findUserStringByLogin(String login) throws UserNotFoundException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(authFile)))) {
            Optional<String[]> userStringArray = reader.lines()
                    .map(s -> s.split(" "))
                    .filter(s -> s[1].equals(login))
                    .findFirst();
            return userStringArray.orElseThrow(() -> new UserNotFoundException(String.format("User with login '%s' isn't exist", login)));
        } catch (FileNotFoundException e) {
            logger.fatal(e);
        } catch (IOException e) {
            logger.error(String.format("Authorization file '%s' can not be read", authFile.getAbsolutePath()));
        }
        throw new UserNotFoundException(String.format("User with login '%s' isn't exist", login));
    }

    @Override
    public int getUserIdByLogin(String login) throws UserNotFoundException {
        String[] userString = findUserStringByLogin(login);
        return Integer.parseInt(userString[0]);
    }
}
