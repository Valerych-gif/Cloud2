package ru.valerych.cloud2.client.services.fileservices;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.valerych.cloud2.client.entities.FileInfo;
import ru.valerych.cloud2.client.entities.TestConnection;
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.commands.Responses;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.valerych.cloud2.client.utils.ConnectionsUtils.createConnection;
import static ru.valerych.cloud2.client.utils.ConnectionsUtils.getConnection;

class RemoteFileExplorerTest {

    @Test
    void getFileList() throws IOException {
        RemoteFileExplorer fileExplorer = new RemoteFileExplorer("");
        TestConnection testConnection = createConnection();
        CloudConnection connection = getConnection(testConnection);
        fileExplorer.connectionUpdate(connection);

        DataInputStream serverIs = new DataInputStream(testConnection.getServerSocket().getInputStream());
        DataOutputStream serverOs = new DataOutputStream(testConnection.getSocket().getOutputStream());

        sendData(serverIs, serverOs);

        new Thread(()->{
            ObservableList<FileInfo> fileInfoObservableList = fileExplorer.getFileList();
            List<String> fileNames = fileInfoObservableList.stream().map(FileInfo::getFileName).collect(Collectors.toList());
            Assertions.assertEquals(5, fileInfoObservableList.size());
            Assertions.assertTrue(fileNames.contains("testFile1"));
            Assertions.assertTrue(fileNames.contains("testDirectory"));
        }).start();
    }

    private void sendData(DataInputStream serverIs, DataOutputStream serverOs) {
        new Thread(()->{
            try {
                serverIs.readByte();
                serverOs.writeByte(Responses.OK.getSignalByte());
                int directoryNameLength = serverIs.readByte();
                serverOs.writeByte(Responses.OK.getSignalByte());
                byte[] directoryNameBytes = new byte[directoryNameLength];
                int read = serverIs.read(directoryNameBytes);

                createFileInfoList().forEach(fileInfo -> {
                    try {
                        serverOs.writeByte(Responses.SEND_FILE_INFO.getSignalByte());
                        serverOs.writeByte(fileInfo.getFileName().length());
                        serverOs.write(fileInfo.getFileName().getBytes(StandardCharsets.UTF_8));
                        byte mark = fileInfo.isDirectory()?(byte) 'D':(byte) 'F';
                        serverOs.writeByte(mark);
                        serverOs.writeLong(fileInfo.getSize());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private List<FileInfo> createFileInfoList(){
        List<FileInfo> fileInfoList = new ArrayList<>();
        fileInfoList.add(new FileInfo("testFile1", "ext", 10L, "2021-04-13", "rrw", false));
        fileInfoList.add(new FileInfo("testFile2", "ext", 15L, "2021-04-13", "rrw", false));
        fileInfoList.add(new FileInfo("testFile3", "ext", 20L, "2021-04-13", "rrw", false));
        fileInfoList.add(new FileInfo("testFile4", "ext", 30L, "2021-04-13", "rrw", false));
        fileInfoList.add(new FileInfo("testDirectory", "", 0L, "2021-04-13", "rrw", true));
        return fileInfoList;
    }

}