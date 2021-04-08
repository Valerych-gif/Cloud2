package ru.valerych.cloud2.client.services.fileservices;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.entities.FileInfo;
import ru.valerych.cloud2.client.utils.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFileExplorer {

    private static final Logger logger = LogManager.getLogger(LocalFileExplorer.class.getName());

    private final String DEFAULT_ROOT_DIRECTORY = "./Cloud2Directory";
    private final String LEFT_PANEL_ROOT_DIRECTORY_PROPERTY = "left-panel-current-directory";
    private final String RIGHT_PANEL_ROOT_DIRECTORY_PROPERTY = "right-panel-current-directory";
    private final String LEFT_PANEL_ID = "leftPanel";
    private final String RIGHT_PANEL_ID = "rightPanel";

    private Path currentDirectory;
    private final String currentDirectorySettingName;

    public LocalFileExplorer(String PaneId) {
        currentDirectorySettingName = LEFT_PANEL_ID.equals(PaneId) ? LEFT_PANEL_ROOT_DIRECTORY_PROPERTY : RIGHT_PANEL_ROOT_DIRECTORY_PROPERTY;
        setUp();
    }

    public ObservableList<FileInfo> getFileList() {
        ObservableList<FileInfo> fileInfoObservableList = FXCollections.observableArrayList();
        if (!currentDirectory.equals(Paths.get(DEFAULT_ROOT_DIRECTORY))){
            Path parent = currentDirectory.getParent()==null?Paths.get(DEFAULT_ROOT_DIRECTORY):currentDirectory.getParent();
            fileInfoObservableList.add(new FileInfo(parent, ".."));
        }
        try {
            Files.list(currentDirectory)
                    .forEach(p->fileInfoObservableList.add(new FileInfo(p)));
            return FXCollections.observableArrayList(fileInfoObservableList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList();
    }

    private void setUp() {
        String currentDirectoryStrPath = Settings.read(currentDirectorySettingName);
        currentDirectory = currentDirectoryStrPath == null ? Paths.get(DEFAULT_ROOT_DIRECTORY) : Paths.get(currentDirectoryStrPath);
        Settings.write(currentDirectorySettingName, currentDirectory.toString());
        try {
            if (!Files.exists(currentDirectory))
                Files.createDirectory(currentDirectory);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void setCurrentDirectory(Path path) {
        currentDirectory = path;
        Settings.write(currentDirectorySettingName, currentDirectory.toString());
    }
}
