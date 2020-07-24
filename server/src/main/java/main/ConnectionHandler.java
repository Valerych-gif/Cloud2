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

    private Logger logger = LogManager.getLogger(ConnectionHandler.class);

    private DataInputStream is;
    private DataOutputStream os;
    private Socket socket;
    private FileHandler fileHandler;
    private boolean connectionIsActive;

    public ConnectionHandler(Socket socket) throws IOException, InterruptedException {
        logger.info("Connection accepted");
        this.connectionIsActive = true;
        this.socket = socket;
        this.is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());
        Thread.sleep(2000);
    }



    @Override
    public void run() {

        fileHandler = new FileHandler(this);
        while (connectionIsActive) {
            try {
                String command = is.readUTF();
                if (command.equals("./upload")) {
                    receiveFileFromClient();
                } else if (command.equals("./download")){
                    sendFileToClient();
                } else if (command.equals("./close")){
                    closeConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void sendFileToClient() throws IOException {
        String fileName = is.readUTF();
        CloudFile file = new CloudFile(Cloud2ServerStarter.storageRootDir + "/" + fileName);
        fileHandler.getFileFromStorage(file);
    }

    private void receiveFileFromClient() throws IOException {
        String fileName = is.readUTF();
        long fileLength = is.readLong();
        CloudFile file = new CloudFile(Cloud2ServerStarter.storageRootDir + "/" + fileName, fileLength);
        fileHandler.loadFileToStorage(file);
    }

    private void closeConnection() {
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
        connectionIsActive = false;
    }

    public DataInputStream getDataInputStream() {
        return is;
    }

    public DataOutputStream getDataOutputStream() {
        return os;
    }
}
