package ru.valerych.cloud2.servers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.utils.LogUtils;
import ru.valerych.cloud2.utils.SetUpUtils;

import java.io.File;

public abstract class Cloud2Server {

    protected static File storage;
    protected static Logger logger = LogManager.getLogger(Cloud2Server.class);
    private static Cloud2Server server;

    protected Cloud2Server() {
        storage = new File(Cloud2ServerSettings.STORAGE_ROOT_DIR);
        try {
            SetUpUtils.setUpServersFiles();
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), logger);
        }
    }

    public static Cloud2Server getInstance() {
        if (Cloud2Server.server==null) {
            if (Cloud2ServerSettings.SERVER_TYPE.equals("IOServer")) {
                Cloud2Server.server = new Cloud2IOServer();
            }
        }
        return Cloud2Server.server;
    }

    private static boolean isFileExist() {
        return storage.exists();
    }

    public File getStorage() {
        return storage;
    }

    public abstract void start();

}
