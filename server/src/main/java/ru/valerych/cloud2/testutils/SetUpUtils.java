package ru.valerych.cloud2.testutils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.servers.Cloud2Server;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

import java.io.File;

public class SetUpUtils {
    protected static Logger logger = LogManager.getLogger(Cloud2Server.class);

    public static void setUpServersFiles() throws Exception {
        File storage = new File(Cloud2ServerSettings.STORAGE_ROOT_DIR);
        File serversMainFilesDir = new File(Cloud2ServerSettings.SERVER_MAIN_FILES_DIR);
        File authFile = new File(Cloud2ServerSettings.SERVER_MAIN_FILES_DIR + "/" + Cloud2ServerSettings.AUTH_FILE);
        File sharedFiles = new File(Cloud2ServerSettings.SERVER_MAIN_FILES_DIR + "/" + Cloud2ServerSettings.SHARED_FILE);

        if (!storage.exists()) {
            if (storage.mkdir()) {
                logger.info("Создана корневая папка сетевого хранилища");
            } else {
                logger.error("Не удалось создать корневую папку сетевого хранилища");
            }
        }

        if (!serversMainFilesDir.exists()) {
            if (serversMainFilesDir.mkdir()) {
                logger.info("Создана корневая папка файлов управления сервером");
            } else {
                logger.error("Не удалось создать корневую папку файлов управления сервером");
            }
        }

        if (!authFile.exists()) {
            if (authFile.createNewFile()) {
                logger.info("Создан файл аутентификации");
            } else {
                logger.error("Не удалось создать файл аутентификации");
            }
        }

        if (!sharedFiles.exists()) {
            if (sharedFiles.createNewFile()) {
                logger.info("Создан файл расшареных файлов");
            } else {
                logger.error("Не удалось создать файл  расшареных файлов");
            }
        }


    }
}
