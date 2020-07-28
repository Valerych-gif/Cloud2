package fakeantities;

import main.Cloud2Server;
import main.Cloud2ServerStarter;
import main.ConnectionHandler;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class FakeServer extends Cloud2Server {

    private ConnectionHandler connectionHandler;
    private static Cloud2Server instance;

    private FakeServer() {
        storage = new File("src/test/resources/storage");
    }

    public static Cloud2Server getInstance() {
        if (instance == null) {
            instance = new FakeServer();
        }
        return instance;
    }

    public void waitConnection() {
        try {
            serverSocket = new ServerSocket(Cloud2ServerStarter.PORT);
            socket = serverSocket.accept();
            connectionHandler = new ConnectionHandler(this, socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(){
        new Thread(()->{
            connectionHandler.run();
        }).start();
    }

    public ConnectionHandler getConnectionHandler() {
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
