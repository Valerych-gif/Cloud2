package ru.valerych.cloud2.client.utils;

import ru.valerych.cloud2.client.entities.TestConnection;
import ru.valerych.cloud2.client.network.CloudConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionsUtils {

    public static TestConnection createConnection() throws IOException {
        TestConnection connection = new TestConnection();
        int tmpPort = Utils.getPort();
        new Thread(()->{
            try {
                ServerSocket serverSocket = new ServerSocket(tmpPort);
                Socket socket = serverSocket.accept();
                connection.setServerSocket(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Socket socket = new Socket("localhost", tmpPort);
        connection.setSocket(socket);
        connection.setLogin("test");
        connection.setPassword("test");
        connection.setAuthorized(true);
        return connection;
    }

    public static CloudConnection getConnection(TestConnection testConnection) throws IOException {
        CloudConnection connection = new CloudConnection();
        connection.setLogin(testConnection.getLogin());
        connection.setPassword(testConnection.getPassword());
        connection.setSocket(testConnection.getSocket());
        connection.setInputStream(new DataInputStream(testConnection.getSocket().getInputStream()));
        connection.setOutputStream(new DataOutputStream(testConnection.getSocket().getOutputStream()));
        connection.setAuthorized(true);
        return connection;
    }
}
