package main;

import io.ConnectionHandler;
import exceptions.CantToCreateStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public abstract class Cloud2Server {

    protected ServerSocket serverSocket;
    protected ExecutorService executor;
    protected Socket socket;
    protected File storage;
    protected Logger logger = LogManager.getLogger(Cloud2Server.class);

    public  Cloud2Server() {
        storage = new File(Cloud2ServerStarter.storageRootDir);
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

    public ConnectionHandler getConnectionHandler(){
        return null;
    }

    public abstract void waitConnection();
}
