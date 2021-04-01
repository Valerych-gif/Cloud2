package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.valerych.cloud2.authservice.IOUsersService;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.fileservices.iofileservices.IOServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import ru.valerych.cloud2.network.ionetwork.IONetworkFactory;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.shareservice.interfaces.ShareFile;
import ru.valerych.cloud2.utils.Client;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.FILE_TO_SHARE;
import static ru.valerych.cloud2.settings.Cloud2ServerSettings.*;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.createUserFile;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.removeFileStructure;

class IOShareFileTest {

    private static Network network;
    private static Client client;

    private static ServerSocket serverSocket;
    private static final Path shareFilePath = Paths.get(SERVER_MAIN_FILES_DIR, SHARED_FILE);


    @BeforeAll
    public static void ioServicesInit() {

        try {
            serverSocket = new ServerSocket(Cloud2ServerSettings.PORT);
            new Thread(() -> client = new Client()).start();
            Socket socket = serverSocket.accept();
            NetworkFactory networkFactory = new IONetworkFactory();
            network = networkFactory.createNetwork(socket);

            Files.deleteIfExists(IOUsersService.AUTH_FILE_PATH);
            Files.createFile(IOUsersService.AUTH_FILE_PATH);
            Files.write(IOUsersService.AUTH_FILE_PATH, ("0 testSender test" + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            Files.write(IOUsersService.AUTH_FILE_PATH, ("1 testReceiver test"+ System.lineSeparator()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

            Files.deleteIfExists(shareFilePath);
            Files.createFile(shareFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void clearResources() throws IOException {
        Files.deleteIfExists(IOUsersService.AUTH_FILE_PATH);
        Files.deleteIfExists(shareFilePath);
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
    void shareFileByCommandFromClient() throws IOException {
        User user1 = new User(0, "testSender", "test");
        User user2 = new User(1, "testReceiver", "test");
        user1.setUpUser();
        user2.setUpUser();
        File userFile = createUserFile();
        client.sendBytesToServer(new byte[]{(byte) user2.getLogin().length()});
        client.sendBytesToServer(user2.getLogin().getBytes(StandardCharsets.UTF_8));
        client.sendBytesToServer(new byte[]{(byte) userFile.getName().length()});
        client.sendBytesToServer(userFile.getName().getBytes(StandardCharsets.UTF_8));
        ServerFileExplorer serverFileExplorer = new IOServerFileExplorer(user1);
        ShareFile shareFile = new IOShareFile(user1, network, serverFileExplorer);
        shareFile.shareFileByCommandFromClient();
        List<String> strings = Files.lines(shareFilePath).collect(Collectors.toList());
        String sharedString = "0 1 src"+FILE_SEPARATOR + "test" + FILE_SEPARATOR + "storage" + FILE_SEPARATOR + "0" +FILE_SEPARATOR + "userFile.txt";
        Assertions.assertTrue(strings.contains(sharedString));
    }
}