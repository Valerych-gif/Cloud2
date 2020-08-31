package entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import utils.LogUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class User {

    private String login;
    private String password;
    private int id;

    private File userStorage;

    private Logger logger = LogManager.getLogger(User.class);

    public void setUpNewUser() {
        Path userStoragePath = Paths.get(Cloud2ServerSettings.STORAGE_ROOT_DIR, String.valueOf(id));
        userStorage = userStoragePath.toFile();
        if (!userStorage.exists()) {
            if (userStorage.mkdir()) {
                logger.info("Создана корневая папка пользователя " + login);
            } else {
                LogUtils.error("Создать корневую папку пользователя не удалось", logger);
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
}
