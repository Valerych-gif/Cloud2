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
    private FileHandler fileHandler;

    public ConnectionHandler(Socket socket) throws IOException, InterruptedException {
        logger.info("Connection accepted");
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        Thread.sleep(2000);
    }



    @Override
    public void run() {

        fileHandler = new FileHandler(this);
        while (true) {
            try {
                String command = is.readUTF();
                if (command.equals("./upload")) {
                    String fileName = is.readUTF();
                    System.out.println("fileName: " + fileName);
                    long fileLength = is.readLong();
                    System.out.println("fileLength: " + fileLength);
                    CloudFile file = new CloudFile(Cloud2ServerStarter.storageRootDir + "/" + fileName, fileLength);
                    fileHandler.loadFileToStorage(file);
                    os.writeUTF("OK");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public DataInputStream getDataInputStream() {
        return is;
    }

    public DataOutputStream getDataOutputStream() {
        return os;
    }
}
