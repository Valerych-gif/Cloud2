package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public abstract class ConnectionHandler implements Runnable {

    protected Logger logger = LogManager.getLogger(ConnectionHandler.class);

    protected Cloud2Server server;
    protected FileHandler fileHandler;
    protected File storage;
    protected boolean isConnectionActive;
    protected Commands command;

    public ConnectionHandler(Cloud2Server server) {
        logger.info("Connection accepted");
        this.isConnectionActive = true;
        this.server = server;
        this.storage = server.getStorage();
        this.command=null;
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

    protected abstract File getStorage();

    public abstract String getStringFromClient();
}
