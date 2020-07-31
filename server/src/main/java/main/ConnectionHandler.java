package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    protected Commands command;

    public ConnectionHandler(Cloud2Server server) {
        logger.info("Connection accepted");
        isConnectionActive = true;
        this.server = server;
        this.storage = server.getStorage();
    }

    @Override
    public void run() {

        try {
            if (command.equals(Commands.UPLOAD)) {
                sendResponse(Responses.OK.responseStr);
                receiveFileFromClient();
            } else if (command.equals(Commands.DOWNLOAD)) {
                sendFileToClient();
            } else if (command.equals(Commands.CLOSE_CONNECTION)) {
                closeConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void sendResponse(String responseStr);

    protected abstract void closeConnection();

    protected abstract void sendFileToClient() throws IOException;

    protected abstract void receiveFileFromClient() throws IOException;

    public abstract File getStorage();

    public abstract DataInputStream getDataInputStream();

    public abstract DataOutputStream getDataOutputStream();
}
