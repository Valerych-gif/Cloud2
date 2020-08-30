package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servers.Cloud2Server;
import settings.Cloud2ServerSettings;

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
                LogUtils.info("Создана корневая папка сетевого хранилища", logger);
            } else {
                LogUtils.error("Не удалось создать корневую папку сетевого хранилища", logger);
            }
        }

        if (!serversMainFilesDir.exists()) {
            if (serversMainFilesDir.mkdir()) {
                LogUtils.info("Создана корневая папка файлов управления сервером", logger);
            } else {
                LogUtils.error("Не удалось создать корневую папку файлов управления сервером", logger);
            }
        }

        if (!authFile.exists()) {
            if (authFile.createNewFile()) {
                LogUtils.info("Создан файл аутентификации", logger);
            } else {
                LogUtils.error("Не удалось создать файл аутентификации", logger);
            }
        }

        if (!sharedFiles.exists()) {
            if (sharedFiles.createNewFile()) {
                LogUtils.info("Создан файл расшареных файлов", logger);
            } else {
                LogUtils.error("Не удалось создать файл  расшареных файлов", logger);
            }
        }


    }
}
