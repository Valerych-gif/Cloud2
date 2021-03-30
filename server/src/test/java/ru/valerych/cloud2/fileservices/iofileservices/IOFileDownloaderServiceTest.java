package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.FileDownloaderService;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import ru.valerych.cloud2.network.ionetwork.IONetworkFactory;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.utils.Client;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.USER_FILE;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.createFilledUserFile;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.removeFileStructure;

class IOFileDownloaderServiceTest {

    private static Network network;
    private static Client client;
    private static FileDownloaderService fileDownloaderService;
    private static ServerSocket serverSocket;

    @BeforeAll
    public static void ioServicesInit() {

        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
            new Thread(() -> client = new Client()).start();
            Socket socket = serverSocket.accept();
            NetworkFactory networkFactory = new IONetworkFactory();
            network = networkFactory.createNetwork(socket);
            User user = new User(0, "test", "test");
            user.setUpUser(Paths.get("../storage/0"));
            ServerFileExplorer serverFileExplorer = new IOServerFileExplorer(user);
            fileDownloaderService = new IOFileDownloaderService(network, serverFileExplorer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void clearResources() {
        removeFileStructure();
        try {
            serverSocket.close();
            network.closeConnection();
            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Sending of file to client is success")
    void sendFileToClientSuccessTest() {
        File userFile = createFilledUserFile();
        client.sendBytesToServer(new byte[]{Byte.parseByte(String.valueOf(USER_FILE.length()))});
        client.sendBytesToServer(USER_FILE.getBytes());
        fileDownloaderService.sendFileToClient();
        byte type = client.getBytesFromServer(1)[0];
        long fileSize = client.getLong();
        Assertions.assertEquals(70, type);
        Assertions.assertEquals(userFile.length(), fileSize);
    }
}