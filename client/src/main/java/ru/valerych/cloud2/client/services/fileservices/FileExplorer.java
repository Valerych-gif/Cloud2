package ru.valerych.cloud2.client.services.fileservices;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.entities.FileInfo;
import ru.valerych.cloud2.client.utils.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileExplorer {

    private static final Logger logger = LogManager.getLogger(FileExplorer.class.getName());

    private final String DEFAULT_ROOT_DIRECTORY = "./Cloud2Directory";
    private final String LEFT_PANEL_ROOT_DIRECTORY_PROPERTY = "left-panel-current-directory";
    private final String RIGHT_PANEL_ROOT_DIRECTORY_PROPERTY = "right-panel-current-directory";
    private final String LEFT_PANEL_ID = "leftPanel";
    private final String RIGHT_PANEL_ID = "rightPanel";

    private Path currentDirectory;
    private final VBox panel;
    private final String currentDirectorySettingName;

    public FileExplorer(VBox panel) {
        this.panel = panel;
        currentDirectorySettingName = LEFT_PANEL_ID.equals(panel.getId()) ? LEFT_PANEL_ROOT_DIRECTORY_PROPERTY : RIGHT_PANEL_ROOT_DIRECTORY_PROPERTY;
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
