package nio;

import files.CloudFile;
import main.FileHandler;
import main.ConnectionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOFileHandler extends FileHandler {

    private ServerSocketChannel serverSocketChannel;
    private SocketChannel socketChannel;

    public NIOFileHandler(ConnectionHandler connectionHandler) {
        super(connectionHandler);
    }

    @Override
    public void init() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8190));
            socketChannel = serverSocketChannel.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean getFileFromStorage(String file) {
        return false;
    }

    @Override
    public boolean loadFileToStorage(CloudFile file) {
        RandomAccessFile aFile = null;
        try {
            aFile = new RandomAccessFile(file.getAbsolutePath(), "rw");
            FileChannel channel = aFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int bytesRead = socketChannel.read(buf);
            while (bytesRead != -1) {
                buf.flip();
                channel.write(buf);
                buf.clear();
                bytesRead = socketChannel.read(buf);
            }
            aFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void sendDirContentToClient() {

    }
}
