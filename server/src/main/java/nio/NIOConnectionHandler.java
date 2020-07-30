package nio;

import files.CloudFile;
import main.Cloud2Server;
import main.Cloud2ServerStarter;
import main.ConnectionHandler;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

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
                command = getCommand();
                super.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getCommand() throws IOException {
        int bytesRead = socketChannel.read(buffer);
        System.out.println("up");
        StringBuilder c = new StringBuilder();
        boolean commandIsNotEnd = true;
        while (bytesRead != -1&&commandIsNotEnd) {
            buffer.flip();
            while(buffer.hasRemaining()){
                char ch = (char) buffer.get();
                if ((int)ch!=0) {
                    System.out.println(ch + " " + (int)ch);
                    c.append(ch);
                } else {
                    commandIsNotEnd = false;
                    break;
                }
            }
            buffer.clear();
            if (commandIsNotEnd) bytesRead = socketChannel.read(buffer);
        }
        return c.toString();
    }

    public void sendFileToClient() throws IOException {
        socketChannel.read(buffer);
        String fileName = buffer.toString();
        CloudFile file = new CloudFile(storage + "/" + fileName);
        fileHandler.getFileFromStorage(file);
    }

    public void receiveFileFromClient() throws IOException {
        String fileName = getCommand();
        System.out.println(fileName);
        long fileLength = Long.getLong(getCommand());
        System.out.println(fileLength);
        CloudFile file = new CloudFile(storage + "/" + fileName, fileLength);
        System.out.println(file.getAbsolutePath());
        //fileHandler.loadFileToStorage(file);
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
