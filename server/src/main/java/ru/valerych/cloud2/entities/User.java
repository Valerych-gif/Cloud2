package ru.valerych.cloud2.entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class User {

    private int id;
    private String login;
    private String password;

    private File userStorage;

    private final Logger logger = LogManager.getLogger(User.class);

    public static final User UNAUTHORIZED_USER = new User(-1, "", "");

    public User(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public void setUpUser() {
        Path userStoragePath = Paths.get(Cloud2ServerSettings.STORAGE_ROOT_DIR, String.valueOf(id));
        setUpUser(userStoragePath);
    }

    public void setUpUser(Path userStoragePath){
        userStorage = userStoragePath.toFile();
        if (!userStorage.exists()) {
            if (userStorage.mkdir()) {
                logger.info("Создана корневая папка пользователя " + login);
            } else {
                logger.error("Создать корневую папку пользователя не удалось");
            }
        }
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public File getUserStorage() {
        return userStorage;
    }
}