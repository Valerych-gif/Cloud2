package utils;

import fakeantities.FakeClient;
import main.Cloud2IOServer;
import main.Cloud2Server;
import main.Cloud2ServerStarter;
import main.ConnectionHandler;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Utils {

    public final static String TEST_STORAGE_ROOT_DIR = "src/test/resources/storage";

    private static Socket socket;
    private static ServerSocket serverSocket;

    public static void recursiveDelete(File file) {
        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }

        file.delete();
    }

    public static ConnectionHandler getConnectionHandler(FakeClient client){
        Cloud2Server testServer = Cloud2IOServer.getInstance();
        Cloud2ServerStarter.setStorageRootDir(TEST_STORAGE_ROOT_DIR);
        testServer.init();
        client.connect();
        ConnectionHandler connectionHandler = null;

        try {
            serverSocket = new ServerSocket(Cloud2ServerStarter.PORT);
            socket = serverSocket.accept();
            connectionHandler = new ConnectionHandler(socket);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectionHandler;
    }

    public static ConnectionHandler killConnectionHandler(FakeClient client) {
        try {
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
