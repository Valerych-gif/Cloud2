package main;

import auth.AuthService;
import exceptions.CantToCreateStorageException;
import io.IOFileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public abstract class ConnectionHandler implements Runnable {

    protected Logger logger = LogManager.getLogger(ConnectionHandler.class);

    protected Cloud2Server server;
    protected ConnectionHandler connectionHandler;
    protected FileHandler fileHandler;
    protected AuthService authService;
    protected String userStoragePath;
    protected File mainStorage;
    protected File userStorage;
    protected boolean isConnectionActive;
    protected Commands command;
    protected String login;
    protected String pass;
    protected int userId;

    public ConnectionHandler(Cloud2Server server) {
        logger.info("Connection accepted");
        this.isConnectionActive = true;
        this.server = server;
        this.authService = AuthService.getInstance();
        this.mainStorage = server.getStorage();
        this.command = null;
        this.userId=-1;
    }

    public void authorization() {
        getLoginAndPassFromClient();
        if (login!=null) {
            try {
                String userIdStr = authService.getId(login, pass);
                this.userId = userIdStr != null ? Integer.parseInt(userIdStr) : -1;
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    public void registration(){
        getLoginAndPassFromClient();
        if (login!=null) {
            try {
                String userIdStr = authService.registration(login, pass);
                this.userId = userIdStr != null ? Integer.parseInt(userIdStr) : -1;
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    public void userInit() {
        if (userId!=-1) {
            userStoragePath = mainStorage.getAbsolutePath() + "\\" + userId;
            this.userStorage = new File(userStoragePath);
            try {
                setUpUserStorage();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    public void setUpUserStorage() throws Exception {
        if (userStorage.exists()) return;
        if (userStorage.mkdir()) {
            logger.info("Создана корневая папка пользователя " + login);
        } else {
            throw new CantToCreateStorageException();
        }
    }

    @Override
    public void run() {
        while (isConnectionActive) {
            command = null;
            while (command == null) {
                command = getCommandFromClient(); // Block
            }
            try {
                switch (command) {
                    case AUTHORIZATION:
                        sendResponse(Responses.OK.getString());
                        authorization();
                        if (userId!=-1){
                            sendResponse(Responses.OK.getString());
                            seUpUser();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case REGISTRATION:
                        sendResponse(Responses.OK.getString());
                        registration();
                        if (userId!=-1){
                            sendResponse(Responses.OK.getString());
                            seUpUser();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case UPLOAD:
                        if (userId!=-1) {
                            sendResponse(Responses.OK.getString());
                            receiveFileFromClient(); // Block
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case DOWNLOAD:
                        if (userId!=-1) {
                            sendResponse(Responses.OK.getString());
                            sendFileToClient();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case GET_DIR_CONTENT:
                        if (userId!=-1) {
                            sendResponse(Responses.OK.getString());
                            sendDirContent();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case CLOSE_CONNECTION:
                        if (userId!=-1) {
                            sendResponse(Responses.OK.getString());
                            closeConnection();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                }
            } catch (Exception e) {
                closeConnection();
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    protected abstract void seUpUser();

    protected abstract void sendDirContent();

    protected abstract Commands getCommandFromClient();

    public abstract void sendResponse(String responseStr);

    protected abstract void closeConnection();

    protected abstract void sendFileToClient() throws IOException;

    protected abstract void receiveFileFromClient() throws IOException;

    public abstract String getStringFromClient();

    protected abstract void getLoginAndPassFromClient();

    public File getMainStorage() {
        return mainStorage;
    }

    public File getUserStorage() {
        return userStorage;
    }
}
