package ru.valerych.cloud2.client.services.fileservices;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.entities.FileInfo;
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.client.network.ConnectionObserver;
import ru.valerych.cloud2.client.network.Network;
import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.commands.Responses;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SharedFileExplorer implements ConnectionObserver {

    private static final Logger logger = LogManager.getLogger(SharedFileExplorer.class.getName());

    private CloudConnection connection;

    public SharedFileExplorer(){

    }

    public ObservableList<FileInfo> getFileList() {

        ObservableList<FileInfo> fileInfoObservableList = FXCollections.observableArrayList();
        List<FileInfo> fileInfoList = null;
        try {
            fileInfoList = getFileInfoList();
        } catch (BadResponseException e) {
            logger.error(e);
        }
        if (fileInfoList!=null) fileInfoObservableList.addAll(fileInfoList);
        return FXCollections.observableArrayList(fileInfoObservableList);
    }

    private List<FileInfo> getFileInfoList() throws BadResponseException {
        if (!connection.isAuthorized()) throw new RuntimeException("You are disconnected from server");
        List<FileInfo> fileInfoList = new ArrayList<>();
        Network network = new Network(connection);

        network.sendByteToServer(Requests.GET_SHARED_DIR_CONTENT.get());
        if (network.readByteFromServer() == Responses.FAIL.getSignalByte()) throw new BadResponseException("Can't get shared directory content");

        byte signalByte = network.readByteFromServer();
        while (signalByte==Responses.SEND_FILE_INFO.getSignalByte()){
            byte fileNameLength = network.readByteFromServer();

            byte[] fileNameBytes = network.readBytesFromServer(fileNameLength);
            String fileName = new String(fileNameBytes);

            byte mark = network.readByteFromServer();
            boolean isDirectory = mark == 'D';

            long fileSize = network.readLongFromServer();

            fileInfoList.add(new FileInfo(fileName, "ext", fileSize, "", "", isDirectory));
            signalByte = network.readByteFromServer();
        }

        return fileInfoList;
    }

    @Override
    public void connectionUpdate(CloudConnection connection) {
        logger.debug("RemoteFileExplorer. Connection status was changed. Authorized: " + connection.isAuthorized());
        this.connection = connection;

    }
}
