package nio;

import files.CloudFile;
import io.IOFileHandler;
import main.Cloud2Server;
import main.Cloud2ServerStarter;
import main.Commands;
import main.ConnectionHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class NIOConnectionHandler extends ConnectionHandler {

    private SocketChannel socketChannel;
    private Selector selector;
    private ByteBuffer buffer;
    private SelectionKey key;

    public NIOConnectionHandler(Cloud2Server server, SocketChannel socketChannel){
        super(server);
        this.socketChannel = socketChannel;
        try {
            selector = Selector.open();
            key = socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        fileHandler = new NIOFileHandler(this);
        while (isConnectionActive) {
            command = getCommandFromClient(); // Block
            super.run();
        }
    }

    @Override
    public void sendResponse(String responseStr) {

    }

    public Commands getCommandFromClient(){

        return null;
    }



    public void sendFileToClient() throws IOException {

    }

    public void receiveFileFromClient() throws IOException {

    }

    public void closeConnection() {

        isConnectionActive = false;
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public File getStorage() {
        return storage;
    }
}
