package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.FileDownloaderService;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.fileservices.iofileservices.IOFileDownloaderService;
import ru.valerych.cloud2.fileservices.iofileservices.IOServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.network.interfaces.NetworkFactory;
import ru.valerych.cloud2.network.ionetwork.IONetworkFactory;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.shareservice.interfaces.SharedFilesDirectoryContentSender;
import ru.valerych.cloud2.utils.Client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.removeFileStructure;

class IOSharedFilesDirectoryContentSenderTest {


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
    @DisplayName("Sending shared files directory content is success")
    void sendSharedFilesDirectoryContentSuccessTest() throws FileNotFoundException {
        User user = new User(0, "test", "test");
        user.setUpUser(Paths.get("../storage/0")); //TODO Перенести в папку test
        SharedFilesDirectoryContentSender contentSender = new IOSharedFilesDirectoryContentSender(user, network);
        contentSender.sendSharedFilesDirectoryContent();
        List<FileInfo> fileInfoList = new ArrayList<>();
        while (true){
            byte fileLength = client.getBytesFromServer(1)[0];
            if (fileLength== Responses.END_OF_DIR_CONTENT.getSignalByte()) break;
            byte[] fileNameBytes = client.getBytesFromServer(fileLength);
            String fileName = new String(fileNameBytes);
            byte fileMark = client.getBytesFromServer(1)[0];
            long fileSize = client.getLong();
            fileInfoList.add(new FileInfo(fileName, fileSize, fileMark==(byte)'D'? FileInfo.Type.DIRECTORY: FileInfo.Type.FILE));
        }
        List<String> fileNames = fileInfoList.stream().map(FileInfo::getFileName).collect(Collectors.toList());
        Assertions.assertTrue(fileNames.contains("test.txt"));
    }
}