package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.authservice.IOUsersService;
import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import ru.valerych.cloud2.network.ionetwork.IONetworkFactory;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.shareservice.interfaces.SharedFilesDirectoryContentSender;
import ru.valerych.cloud2.utils.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.USER_FILE;
import static ru.valerych.cloud2.settings.Cloud2ServerSettings.*;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.createUserFile;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.removeFileStructure;

class IOSharedFilesDirectoryContentSenderTest {

    private static Network network;
    private static Client client;

    private static ServerSocket serverSocket;

    private static final Path SHARE_FILE_PATH = Paths.get(SERVER_MAIN_FILES_DIR, SHARED_FILE);

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

            Files.deleteIfExists(SHARE_FILE_PATH);
            Files.createFile(SHARE_FILE_PATH);
            String sharedString = "0 1 src"+FILE_SEPARATOR + "test" + FILE_SEPARATOR + "storage" + FILE_SEPARATOR + "0" +FILE_SEPARATOR + USER_FILE;
            Files.write(SHARE_FILE_PATH, sharedString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void clearResources() throws IOException {
        removeFileStructure();
        Files.deleteIfExists(SHARE_FILE_PATH);
        Files.deleteIfExists(IOUsersService.AUTH_FILE_PATH);
        try {
            serverSocket.close();
            network.closeConnection();
            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Sending shared files directory content is success")
    void sendSharedFilesDirectoryContentSuccessTest() throws FileNotFoundException {
        User user1 = new User(0, "testSender", "test");
        User user2 = new User(1, "testReceiver", "test");
        user1.setUpUser();
        user2.setUpUser();
        File userFile = createUserFile();

        SharedFilesDirectoryContentSender contentSender = new IOSharedFilesDirectoryContentSender(user2, network);
        contentSender.sendSharedFilesDirectoryContent();
        List<FileInfo> fileInfoList = new ArrayList<>();
        while (client.getBytesFromServer(1)[0]==Responses.SEND_FILE_INFO.getSignalByte()){
            byte fileLength = client.getBytesFromServer(1)[0];
            byte[] fileNameBytes = client.getBytesFromServer(fileLength);
            String fileName = new String(fileNameBytes);
            byte fileMark = client.getBytesFromServer(1)[0];
            long fileSize = client.getLong();
            fileInfoList.add(new FileInfo(fileName, fileSize, fileMark==(byte)'D'? FileInfo.Type.DIRECTORY: FileInfo.Type.FILE));
        }
        List<String> fileNames = fileInfoList.stream()
                .map(FileInfo::getFileName)
                .map(s -> {
                    String fileName = s.substring(s.length()-userFile.getName().length());
                    System.out.println(fileName);
                    return fileName;
                })
                .collect(Collectors.toList());
        Assertions.assertTrue(fileNames.contains(USER_FILE));
    }
}