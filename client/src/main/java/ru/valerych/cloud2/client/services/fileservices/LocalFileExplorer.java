package ru.valerych.cloud2.client.services.fileservices;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.entities.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static ru.valerych.cloud2.client.controllers.MainWindowController.DEFAULT_LOCAL_ROOT_DIRECTORY;

public class LocalFileExplorer {

    private static final Logger logger = LogManager.getLogger(LocalFileExplorer.class.getName());

    private Path currentDirectory;

    public LocalFileExplorer(String currentDirectory) {
        this.currentDirectory = Paths.get(currentDirectory);
        setUp();
    }

    public ObservableList<FileInfo> getFileList() {
        ObservableList<FileInfo> fileInfoObservableList = FXCollections.observableArrayList();
        if (!currentDirectory.equals(Paths.get(""))){
            fileInfoObservableList.add(new FileInfo(Paths.get(""), ".."));
        }
        try {
            Path current = Paths.get(DEFAULT_LOCAL_ROOT_DIRECTORY, currentDirectory.toString());
            List<Path> pathList = Files.list(current)
                    .collect(Collectors.toList());
            if (pathList.size()>0) pathList.forEach(p->fileInfoObservableList.add(new FileInfo(p)));
            return FXCollections.observableArrayList(fileInfoObservableList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList();
    }

    private void setUp() {
        if (!Files.exists(Paths.get(DEFAULT_LOCAL_ROOT_DIRECTORY, currentDirectory.toString())))
            currentDirectory = Paths.get("");
    }

    public void setCurrentDirectory(String path) {
        if (".".equals(path)){
            currentDirectory = Paths.get("");
            return;
        }
        if ("..".equals(path)){
            Path parent = currentDirectory.getParent();
            currentDirectory = parent!=null?parent:Paths.get("");
            return;
        }
        currentDirectory = Paths.get(currentDirectory.getFileName().toString() + System.getProperty("file.separator") + path);
    }

    public Path getCurrentDirectory() {
        return currentDirectory;
    }
}
