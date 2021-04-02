package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.DirectoryContentSender;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import ru.valerych.cloud2.network.ionetwork.IONetworkFactory;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.utils.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.USER_FILE;
import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.USER_INNER_DIRECTORY;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.*;

public class IODirectoryContentSenderTest {

    private static Network network;
    private static Client client;
    private static ServerSocket serverSocket;

    @BeforeAll
    public static void ioServicesInit() {

        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
            new Thread(() -> client = new Client()).start();
            Socket socket = serverSocket.accept();
            NetworkFactory networkFactory = new IONetworkFactory();
            network = networkFactory.createNetwork(socket);
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
    @DisplayName("Sending directory content to client is success")
    public void sendDirectoryContentSuccessTest() {

        createUserDirectory();
        createUserFile();

        User user = new User(0, "test", "test");
        user.setUpUser();
        ServerFileExplorer serverFileExplorer = new IOServerFileExplorer(user);
        DirectoryContentSender directoryContentSender = new IODirectoryContentSender(network, serverFileExplorer);

        client.sendBytesToServer(new byte[]{0});
        client.sendBytesToServer("".getBytes());

        directoryContentSender.sendDirectoryContent();
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            byte fileNameLength = client.getBytesFromServer(1)[0];
            if (fileNameLength == Responses.END_OF_DIR_CONTENT.getSignalByte())
                break;
            String fileName = new String(client.getBytesFromServer(fileNameLength));
            byte fileType = client.getBytesFromServer(1)[0]; // Сливаем вникуда
            Long fileSize = client.getLong(); // Сливаем вникуда
            stringBuilder.append(fileName);
        }
        String string = stringBuilder.toString();
        Assertions.assertTrue(string.contains(USER_INNER_DIRECTORY));
        Assertions.assertTrue(string.contains(USER_FILE));
    }
}
