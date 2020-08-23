package main;

import io.Cloud2IOServer;
import io.IOConnectionHandler;
import exceptions.CantToCreateStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.Socket;

public abstract class Cloud2Server {

    protected Socket socket;
    protected File storage;
    protected static Logger logger = LogManager.getLogger(Cloud2Server.class);
    private static Cloud2Server server;

    public Cloud2Server() {
        storage = new File(Cloud2ServerStarter.STORAGE_ROOT_DIR);
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

    public static Cloud2Server getInstance() {
        return server;
    }

    public void init() {
        try {
            setUpMainStorage();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void setUpMainStorage() throws Exception {
        if (isMainStorageExist()) return;
        if (storage.mkdir()) {
            logger.info("Создана корневая папка сетевого хранилища");
        } else {
            throw new CantToCreateStorageException();
        }
    }

    public boolean isMainStorageExist() {
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
