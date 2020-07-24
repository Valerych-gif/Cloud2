package main;

import exceptions.CantToCreateStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Cloud2Server {

    protected ServerSocket serverSocket;
    protected ExecutorService executor;
    protected Socket socket;
    protected File storage;
    private Logger logger = LogManager.getLogger(Cloud2Server.class);

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

    public void waitConnection() {
        try {
            serverSocket = new ServerSocket(Cloud2ServerStarter.PORT);
            executor = Executors.newCachedThreadPool();
            logger.info("Server started.");
            while (true) {
                socket = serverSocket.accept();
                executor.execute(new ConnectionHandler(this, socket));
                logger.info("Client connected");
            }
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

    public abstract void closeConnection();
}
