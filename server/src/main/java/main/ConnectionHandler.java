package main;

import files.CloudFile;
import files.FileHandler;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

@Getter
public class ConnectionHandler implements Runnable {

    public static String DOWNLOAD_COMMAND = "./download";
    public static String UPLOAD_COMMAND = "./upload";
    public static String CLOSE_CONNECTION_COMMAND = "./closeconnection";

    private Logger logger = LogManager.getLogger(ConnectionHandler.class);

    private Cloud2Server server;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private FileHandler fileHandler;
    private File storage;
    private boolean isConnectionActive;

    public ConnectionHandler(Cloud2Server server, Socket socket) throws IOException{
        logger.info("Connection accepted");
        isConnectionActive = true;
        this.server = server;
        this.socket = socket;
        this.is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());
        this.storage = server.getStorage();
    }



    @Override
    public void run() {

        fileHandler = new FileHandler(this);
        while (isConnectionActive) {
            try {
                String command = is.readUTF();
                if (command.equals(UPLOAD_COMMAND)) {
                    receiveFileFromClient();
                } else if (command.equals(DOWNLOAD_COMMAND)){
                    sendFileToClient();
                } else if (command.equals(CLOSE_CONNECTION_COMMAND)){
                    closeConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void sendFileToClient() throws IOException {
        String fileName = is.readUTF();
        CloudFile file = new CloudFile(storage + "/" + fileName);
        fileHandler.getFileFromStorage(file);
    }

    private void receiveFileFromClient() throws IOException {
        String fileName = is.readUTF();
        long fileLength = is.readLong();
        CloudFile file = new CloudFile(storage + "/" + fileName, fileLength);
        fileHandler.loadFileToStorage(file);
    }

    public void closeConnection() {

        isConnectionActive = false;
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataInputStream getDataInputStream() {
        return is;
    }

    public DataOutputStream getDataOutputStream() {
        return os;
    }

    public Socket getSocket() {
        return socket;
    }

    public File getStorage() {
        return storage;
    }
}
