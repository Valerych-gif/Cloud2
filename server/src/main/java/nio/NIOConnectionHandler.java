package nio;

import files.CloudFile;
import files.FileHandler;
import files.IOFileHandler;
import files.NIOFileHandler;
import io.IOConnectionHandler;
import main.Cloud2Server;
import main.Cloud2ServerStarter;
import main.ConnectionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOConnectionHandler extends ConnectionHandler {

    private SocketChannel socketChannel;
    private ByteBuffer buffer;

    public NIOConnectionHandler(Cloud2Server server, SocketChannel socket) {

        super(server);
        isConnectionActive = true;
        this.socketChannel = socket;
        this.storage = server.getStorage();
        buffer = ByteBuffer.allocate(Cloud2ServerStarter.BUFFER_SIZE);
    }

    @Override
    public void run() {

        fileHandler = new NIOFileHandler(this);

        while (isConnectionActive) {
            try {
                socketChannel.read(buffer);
                String command = buffer.toString();
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



    public void sendFileToClient() throws IOException {
        socketChannel.read(buffer);
        String fileName = buffer.toString();
        CloudFile file = new CloudFile(storage + "/" + fileName);
        fileHandler.getFileFromStorage(file);
    }

    public void receiveFileFromClient() throws IOException {
        socketChannel.read(buffer);
        String[] command = buffer.toString().split(" ");
        String fileName = command[0];
        long fileLength = Long.getLong(command[1]);
        CloudFile file = new CloudFile(storage + "/" + fileName, fileLength);
        fileHandler.loadFileToStorage(file);
    }

    public void closeConnection() {

        isConnectionActive = false;
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketChannel getSocket() {
        return socketChannel;
    }

    public File getStorage() {
        return storage;
    }
}
