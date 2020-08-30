package main;

import io.Cloud2IOServer;
import io.IOConnectionHandler;
import exceptions.CantToCreateStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;

import java.io.File;
import java.net.Socket;

public abstract class Cloud2Server {

    protected Socket socket;
    protected static File storage;
    protected static Logger logger = LogManager.getLogger(Cloud2Server.class);
    private static Cloud2Server server;

    protected Cloud2Server() {
        storage = new File(Cloud2ServerSettings.STORAGE_ROOT_DIR);
    }

    public static Cloud2Server getInstance() {
        try {
            setUpMainStorage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return server;
    }

    public static Cloud2Server getServer(String serverType) {
        if (serverType.equals("IOServer")){
            Cloud2Server.server = Cloud2IOServer.getInstance();
        }
        return server;
    }

    public static void setServer (Cloud2Server server){
        Cloud2Server.server = server;
    }

//    public void init() {
//        try {
//            setUpMainStorage();
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//    }

    public static void setUpMainStorage() throws Exception {
        if (isMainStorageExist()) return;
        if (storage.mkdir()) {
            logger.info("Создана корневая папка сетевого хранилища");
        } else {
            throw new CantToCreateStorageException();
        }
    }

    public static boolean isMainStorageExist() {
        return storage.exists();
    }

    public File getStorage() {
        return storage;
    }

    public IOConnectionHandler getConnectionHandler(){
        return null;
    }

    public abstract void waitConnection();

}
