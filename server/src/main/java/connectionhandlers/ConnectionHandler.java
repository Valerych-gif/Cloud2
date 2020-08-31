package connectionhandlers;

import commands.Commands;
import commands.Responses;
import entities.User;
import authservice.AuthService;
import servers.Cloud2Server;
import filehandlers.FileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.File;
import java.io.IOException;

public abstract class ConnectionHandler implements Runnable {

    private Logger logger = LogManager.getLogger(ConnectionHandler.class);

    protected Cloud2Server server;
    protected ConnectionHandler connectionHandler;
    protected FileHandler fileHandler;
    protected AuthService authService;
    protected File mainStorage;
    protected boolean isConnectionActive;
    protected User user;

    public ConnectionHandler() {
        Cloud2Server server = Cloud2Server.getInstance();
        LogUtils.info("Connection accepted", logger);
        this.isConnectionActive = true;
        this.server = server;
        this.authService = AuthService.getInstance();
        this.mainStorage = server.getStorage();
        this.user=null;
    }

    @Override
    public void run() {
        while (isConnectionActive) {
            Commands command = null;
            try {
                LogUtils.info("Ожидаем сигнальный байт от клиента", logger);
                while (command == null) {
                    command = getCommandFromClient(); // Block
                }
                switch (command) {
                    case AUTHORIZATION:
                        sendResponse(Responses.OK.getString());
                        this.user = authService.getUserByLoginAndPass();
                        if (user!=null){
                            sendResponse(Responses.OK.getString());// TODO move to Network
                        } else {
                            sendResponse(Responses.FAIL.getString());// TODO move to Network
                        }
                        break;
                    case REGISTRATION:
                        sendResponse(Responses.OK.getString());// TODO move to Network
                        this.user = authService.getNewUserByLoginAndPass();
                        if (user!=null){
                            sendResponse(Responses.OK.getString());// TODO move to Network
                            user.setUpNewUser();
                        } else {
                            sendResponse(Responses.FAIL.getString());// TODO move to Network
                        }
                        break;
                    case UPLOAD:
                        if (user!=null) {
                            sendResponse(Responses.OK.getString());
                            //receiveFileFromClient(); // Block
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case DOWNLOAD:
                        if (user!=null) {
                            sendResponse(Responses.OK.getString());
                            //sendFileToClient();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case DELETE:
                        if (user!=null) {
                            sendResponse(Responses.OK.getString());
                            deleteFileFromStorage();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case GET_DIR_CONTENT:
                        if (user!=null) {
                            sendResponse(Responses.OK.getString());
                            sendDirContent();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case GET_SHARED_DIR_CONTENT:
                        if (user!=null) {
                            sendResponse(Responses.OK.getString());
                            sendSharedFilesToClient();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case SHARE:
                        if (user!=null) {
                            sendResponse(Responses.OK.getString());
                            authService.shareFile();
                        } else {
                            sendResponse(Responses.FAIL.getString());
                        }
                        break;
                    case CLOSE_CONNECTION:
                        closeConnection();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                closeConnection();
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    protected abstract void deleteFileFromStorage() throws IOException;

    protected abstract void sendSharedFilesToClient();

    protected abstract void sendDirContent() throws IOException;

    protected abstract Commands getCommandFromClient() throws IOException;

    public abstract void sendResponse(String responseStr);

    public abstract void closeConnection();

}
