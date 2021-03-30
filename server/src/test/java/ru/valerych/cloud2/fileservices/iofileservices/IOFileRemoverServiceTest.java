package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.FileRemoverService;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import ru.valerych.cloud2.network.ionetwork.IONetworkFactory;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.utils.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.*;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.*;

class IOFileRemoverServiceTest {

    private static Network network;
    private static Client client;
    private static ServerSocket serverSocket;

    private static IOServerFileExplorer serverFileExplorer;
    private static FileRemoverService fileRemoverService;

    @BeforeAll
    static void networkInit(){
        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
            new Thread(()-> client = new Client()).start();
            Thread.sleep(500);
            Socket socket = serverSocket.accept();
            NetworkFactory networkFactory = new IONetworkFactory();
            network = networkFactory.createNetwork(socket);

            User user = new User(0, "test", "test");
            user.setUpUser(Paths.get("..", "storage", "0"));
            serverFileExplorer = new IOServerFileExplorer(user);
            fileRemoverService = new IOFileRemoverService(network, serverFileExplorer);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void clearResources(){
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
    @DisplayName("Deleting file is success")
    void deleteFileSuccessTest() throws FileNotFoundException {

        File userFile = createUserFile();
        Assertions.assertTrue(userFile.exists());

        serverFileExplorer.goToDirectory(USER_DIRECTORY);
        client.sendBytesToServer(new byte[]{(byte) USER_FILE.length()});
        client.sendBytesToServer(USER_FILE.getBytes(StandardCharsets.UTF_8));
        fileRemoverService.deleteFile();
        Assertions.assertFalse(userFile.exists());
    }

    @Test
    @DisplayName("Deleting file is fail (File not found)")
    void deleteFileFileNotFoundTest() {

        removeUserFile();

        serverFileExplorer.goToDirectory(USER_DIRECTORY);
        client.sendBytesToServer(new byte[]{(byte) USER_FILE.length()});
        client.sendBytesToServer(USER_FILE.getBytes(StandardCharsets.UTF_8));
        Assertions.assertThrows(FileNotFoundException.class, ()->fileRemoverService.deleteFile());
    }

    @Test
    @DisplayName("Deleting directory is success")
    void deleteDirectorySuccessTest() throws FileNotFoundException {

        File directory = createUserDirectory();
        Assertions.assertTrue(directory.exists());

        serverFileExplorer.goToDirectory(USER_DIRECTORY);
        client.sendBytesToServer(new byte[]{(byte) USER_INNER_DIRECTORY.length()});
        client.sendBytesToServer(USER_INNER_DIRECTORY.getBytes(StandardCharsets.UTF_8));
        fileRemoverService.deleteFile();
        Assertions.assertFalse(directory.exists());
    }
}