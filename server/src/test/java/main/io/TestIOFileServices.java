package main.io;

import commands.Responses;
import entities.User;
import fileserivices.interfaces.DirectoryContentSender;
import fileserivices.interfaces.ServerFileExplorer;
import fileserivices.iofileservices.IODirectoryContentSender;
import fileserivices.iofileservices.IOServerFileExplorer;
import mocks.Client;
import network.interfaces.Network;
import network.interfaces.NetworkFactory;
import network.ionetwork.IONetworkFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import settings.Cloud2ServerSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class TestIOFileServices {

    private static Network network;
    private static Client client;
    private static DirectoryContentSender directoryContentSender;
    private static ServerSocket serverSocket;

    @BeforeAll
    public static void ioServicesInit(){
        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
            new Thread(()->{
                client = new Client();
            }).start();
            Socket socket = serverSocket.accept();
            NetworkFactory networkFactory = new IONetworkFactory();
            network = networkFactory.createNetwork(socket);
            User user = new User(0, "test", "test");
            user.setUpUser(Paths.get("../storage/0"));
            ServerFileExplorer serverFileExplorer = new IOServerFileExplorer(user);
            directoryContentSender = new IODirectoryContentSender(network, serverFileExplorer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void clearResources(){
        try {
            serverSocket.close();
            network.closeConnection();
            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendDirectoryContent(){

        client.sendBytesToServer(new byte[]{0});
        client.sendBytesToServer("".getBytes());
        directoryContentSender.sendDirectoryContent();
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            byte fileNameLength = client.getBytesFromServer(1)[0];
            if (fileNameLength == Responses.END_OF_DIR_CONTENT.getSignalByte())
                break;
            String fileName = new String(client.getBytesFromServer(fileNameLength));
            byte fileType = client.getBytesFromServer(1)[0]; // Сливаем вникуд
            Long fileSize = client.getLong(); // Сливаем вникуда
            stringBuilder.append(fileName);
        }
        String string = stringBuilder.toString();
        Assertions.assertTrue(string.contains("innerDirectory"));
        Assertions.assertTrue(string.contains("TestFile1"));
    }
}
