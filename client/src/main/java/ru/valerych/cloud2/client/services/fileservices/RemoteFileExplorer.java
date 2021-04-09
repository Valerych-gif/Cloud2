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
import ru.valerych.cloud2.client.utils.Settings;
import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.commands.Responses;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RemoteFileExplorer implements ConnectionObserver {

    private static final Logger logger = LogManager.getLogger(RemoteFileExplorer.class.getName());

    private final String DEFAULT_ROOT_DIRECTORY = "";
    private final String LEFT_PANEL_ROOT_DIRECTORY_PROPERTY = "left-panel-remote-current-directory";
    private final String RIGHT_PANEL_ROOT_DIRECTORY_PROPERTY = "right-panel-remote-current-directory";
    private final String LEFT_PANEL_ID = "leftPanel";
    private final String RIGHT_PANEL_ID = "rightPanel";

    private String currentDirectory;
    private final String currentDirectorySettingName;
    private CloudConnection connection;
    private final String paneId;

    public RemoteFileExplorer(String paneId){
        this.paneId = paneId;
        currentDirectory = DEFAULT_ROOT_DIRECTORY;
        if (LEFT_PANEL_ID.equals(paneId)){
            currentDirectorySettingName =  LEFT_PANEL_ROOT_DIRECTORY_PROPERTY;
        } else if (RIGHT_PANEL_ID.equals(paneId)){
            currentDirectorySettingName = RIGHT_PANEL_ROOT_DIRECTORY_PROPERTY;
        } else {
            throw new IllegalArgumentException(paneId);
        }
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
        List<FileInfo> fileInfoList = new ArrayList<>();
        Network network = new Network(connection);

        network.sendByteToServer(Requests.GET_DIR_CONTENT.get());
        if (network.readByteFromServer() == Responses.FAIL.getSignalByte()) throw new BadResponseException("Can't get remote directory content");

        network.sendByteToServer((byte) currentDirectory.length());
        if (network.readByteFromServer() == Responses.FAIL.getSignalByte()) throw new BadResponseException("Can't send remote directory name length");

        network.sendBytesToServer(currentDirectory.getBytes(StandardCharsets.UTF_8));
        if (network.readByteFromServer() == Responses.FAIL.getSignalByte()) throw new BadResponseException("Can't send remote directory name");

        while (network.readByteFromServer()==Responses.SEND_FILE_INFO.getSignalByte()){
            byte fileNameLength = network.readByteFromServer();

            byte[] fileNameBytes = network.readBytesFromServer(fileNameLength);
            String fileName = new String(fileNameBytes);

            byte mark = network.readByteFromServer();
            boolean isDirectory = mark == 'D';

            long fileSize = network.readLongFromServer();

            fileInfoList.add(new FileInfo(fileName, "ext", fileSize, "", "", isDirectory));
        }

        return fileInfoList;
    }

    public void setCurrentDirectory(String path) {
        currentDirectory = path;
        Settings.write(currentDirectorySettingName, currentDirectory);
    }

    @Override
    public void connectionUpdate(CloudConnection connection) {
        logger.debug("RemoteFileExplorer for " + paneId + ". Connection status was changed. Authorized: " + connection.isAuthorized());
        this.connection = connection;

    }
}
