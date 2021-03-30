package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.FileUploaderService;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import ru.valerych.cloud2.network.ionetwork.IONetworkFactory;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.utils.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;

import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.*;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.removeFileStructure;

class IOFileUploaderServiceTest {

    private static Network network;
    private static volatile Client client;
    private static FileUploaderService fileUploaderService;
    private static ServerSocket serverSocket;

    @BeforeAll
    public static void ioServicesInit() {

        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
            new Thread(() -> client = new Client()).start();
            while (client==null);
            Socket socket = serverSocket.accept();
            NetworkFactory networkFactory = new IONetworkFactory();
            network = networkFactory.createNetwork(socket);
            User user = new User(0, "test", "test");
            user.setUpUser(Paths.get("../storage/0"));
            ServerFileExplorer serverFileExplorer = new IOServerFileExplorer(user);
            fileUploaderService = new IOFileUploaderService(network, serverFileExplorer);
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
    @DisplayName("Receiving of file from client is success")
    void receiveFileFromClientSuccessTest() {
        File userFile = new File(FAKE_CLIENT_DIRECTORY + FAKE_CLIENT_TEST_FILE1);
        byte file1Length = (byte) FAKE_CLIENT_TEST_FILE1.length();
        client.sendBytesToServer(new byte[]{file1Length});
        client.sendBytesToServer(FAKE_CLIENT_TEST_FILE1.getBytes(StandardCharsets.UTF_8));
        client.sendLongToServer(userFile.length());
        byte[] clientFileBytes = new byte["test1".length()];
        try (FileInputStream fis = new FileInputStream(userFile)) {
            if (fis.read(clientFileBytes) <= 0) Assertions.fail("There was nothing to read from file " + userFile.getAbsolutePath());
        } catch (Exception e){
            e.printStackTrace();
        }
        client.sendBytesToServer(clientFileBytes);
        fileUploaderService.receiveFileFromClient();
        File storageFile1 = new File(USER_DIRECTORY + FILE_SEPARATOR + FAKE_CLIENT_TEST_FILE1);
        byte[] storageFileBytes = new byte[(int)storageFile1.length()];
        try (FileInputStream fis = new FileInputStream(storageFile1)) {
            if (fis.read(storageFileBytes) <= 0) Assertions.fail("There was nothing to read from file " + userFile.getAbsolutePath());
        } catch (Exception e){
            e.printStackTrace();
        }
        Assertions.assertTrue(storageFile1.exists());
        Assertions.assertEquals(Arrays.toString(clientFileBytes), Arrays.toString(storageFileBytes));
    }
}