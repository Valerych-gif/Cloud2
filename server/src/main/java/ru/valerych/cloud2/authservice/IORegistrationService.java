package ru.valerych.cloud2.authservice;

import ru.valerych.cloud2.authservice.interfaces.RegistrationService;
import ru.valerych.cloud2.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.exceptions.LoginIsNotFreeException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Optional;

import static ru.valerych.cloud2.authservice.IOUsersService.AUTH_FILE_PATH;

public class IORegistrationService implements RegistrationService {

    private final Logger logger = LogManager.getLogger(IORegistrationService.class);
    private final File authFile;

    public IORegistrationService() {
        authFile = AUTH_FILE_PATH.toFile();
    }

    @Override
    public User getNewUserByLoginAndPassword(String login, String password) throws LoginIsNotFreeException {

        if (!isLoginFree(login))
            throw new LoginIsNotFreeException("Login '" + login + "' already in use");

        int userId = getNewUserId();

        User newUser = new User(userId, login, password);
        writeNewUserIntoDB(newUser);
        return newUser;
    }

    synchronized private boolean isLoginFree(String login) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(authFile)))) {
            Optional<String[]> userStringArray = reader.lines()
                    .map(s -> s.split(" "))
                    .filter(s -> s[1].equals(login))
                    .findFirst();
            if (userStringArray.isPresent()) return false;
        } catch (IOException e) {
            logger.error(e);
        }
        return true;
    }

    synchronized private int getNewUserId() {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(authFile)))) {
            Optional<Integer> userStringArray = reader.lines()
                    .map(s -> s.split(" "))
                    .map(s->Integer.parseInt(s[0]))
                    .max(Comparator.comparingInt(a -> a));
            return userStringArray.orElse(0)+1;
        } catch (IOException e) {
            logger.error(e);
        }
        return 1;
    }

    synchronized private void writeNewUserIntoDB(User user) {
        String newUserStr = user.getId() + " " + user.getLogin() + " " + user.getPassword() + System.lineSeparator();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(authFile, true))) {
            writer.write(newUserStr);
            writer.flush();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }
}
