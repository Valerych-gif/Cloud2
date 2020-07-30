package main;

import files.FileHandler;
import files.IOFileHandler;
import io.IOConnectionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public abstract class ConnectionHandler implements Runnable {
    public static String DOWNLOAD_COMMAND = "./download";
    public static String UPLOAD_COMMAND = "./upload";
    public static String CLOSE_CONNECTION_COMMAND = "./closeconnection";

    protected Logger logger = LogManager.getLogger(ConnectionHandler.class);

    protected Cloud2Server server;
    protected FileHandler fileHandler;
    protected File storage;
    protected boolean isConnectionActive;
    protected String command;

    public ConnectionHandler(Cloud2Server server) {
        logger.info("Connection accepted");
        isConnectionActive = true;
        this.server = server;
        this.storage = server.getStorage();
    }

    @Override
    public void run() {

        try {
            if (command.equals(UPLOAD_COMMAND)) {
                receiveFileFromClient();
            } else if (command.equals(DOWNLOAD_COMMAND)) {
                sendFileToClient();
            } else if (command.equals(CLOSE_CONNECTION_COMMAND)) {
                closeConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void closeConnection();

    protected abstract void sendFileToClient() throws IOException;

    protected abstract void receiveFileFromClient() throws IOException;

    public abstract File getStorage();
}
