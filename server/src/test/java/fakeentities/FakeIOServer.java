package fakeentities;

import main.Cloud2Server;
import main.Cloud2ServerStarter;
import io.IOConnectionHandler;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class FakeIOServer extends Cloud2Server {

    private IOConnectionHandler connectionHandler;
    ServerSocket serverSocket;
    private static Cloud2Server instance;

    private FakeIOServer() {
        storage = new File("src/test/resources/storage");
    }

    public static Cloud2Server getInstance() {
        if (instance == null) {
            instance = new FakeIOServer();
        }
        return instance;
    }

    public void waitConnection() {
        try {
            serverSocket = new ServerSocket(Cloud2ServerStarter.PORT);
            socket = serverSocket.accept();
            connectionHandler = new IOConnectionHandler(this, socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(){
        new Thread(()->{
            connectionHandler.run();
        }).start();
    }

    public IOConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    @Override
    public File getStorage() {
        return storage;
    }

    public void closeConnection(){
        try {
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionHandler=null;
    }
}
