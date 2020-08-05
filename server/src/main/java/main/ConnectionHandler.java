package main;

import auth.AuthService;
import exceptions.CantToCreateStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public abstract class ConnectionHandler implements Runnable {

    protected Logger logger = LogManager.getLogger(ConnectionHandler.class);

    protected Cloud2Server server;
    protected FileHandler fileHandler;
    protected AuthService authService;
    protected String userStoragePath;
    protected File mainStorage;
    protected File userStorage;
    protected boolean isConnectionActive;
    protected Commands command;

    public ConnectionHandler(Cloud2Server server) {
        logger.info("Connection accepted");
        this.isConnectionActive = true;
        this.server = server;
        this.authService = AuthService.getInstance();
        this.userStoragePath = authService.getId();
        this.mainStorage = server.getStorage();
        this.userStorage = new File(mainStorage.getAbsolutePath() + "\\" + userStoragePath);
        System.out.println(userStorage.getAbsolutePath());
        try {
            setUpUserStorage();
        } catch (Exception e) {
            logger.error(e);
        }
        this.command=null;
    }

    public void setUpUserStorage() throws Exception {
        if (userStorage.exists()) return;
        if (userStorage.mkdir()) {
            logger.info("Создана корневая папка пользователя " + authService.getLogin());
        } else {
            throw new CantToCreateStorageException();
        }
    }

    @Override
    public void run() {

        while (isConnectionActive) {
            command=null;
            while (command==null) {
                command = getCommandFromClient(); // Block
            }
            try {
                switch (command) {
                    case UPLOAD:
                        sendResponse(Responses.OK.getString());
                        receiveFileFromClient(); // Block
                        break;
                    case DOWNLOAD:
                        sendResponse(Responses.OK.getString());
                        sendFileToClient();
                        break;
                    case GET_DIR_CONTENT:
                        sendResponse(Responses.OK.getString());
                        sendDirContent();
                        break;
                    case CLOSE_CONNECTION:
                        sendResponse(Responses.OK.getString());
                        closeConnection();
                        break;
                }

            } catch (Exception e) {
                closeConnection();
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    protected abstract void sendDirContent();

    protected abstract Commands getCommandFromClient();

    public abstract void sendResponse(String responseStr);

    protected abstract void closeConnection();

    protected abstract void sendFileToClient() throws IOException;

    protected abstract void receiveFileFromClient() throws IOException;

    public abstract String getStringFromClient();

    public File getMainStorage() {
        return mainStorage;
    }

    public File getUserStorage() {
        return userStorage;
    }
}
