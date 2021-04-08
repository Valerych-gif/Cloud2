package ru.valerych.cloud2.client.services.fileservices;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.valerych.cloud2.client.entities.FileInfo;
import ru.valerych.cloud2.client.utils.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileExplorerTest {

    Path testDirectory = Paths.get("test-directory");
    Path innerDirectory1 = Paths.get("test-directory", "innerDirectory1");
    Path innerDirectory2 = Paths.get("test-directory", "innerDirectory2");
    Path innerDirectory3 = Paths.get("test-directory", "innerDirectory3");

    @BeforeEach
    void init() throws IOException {
        if (!Files.exists(testDirectory)) Files.createDirectory(testDirectory);
        if (!Files.exists(innerDirectory1)) Files.createDirectory(innerDirectory1);
        if (!Files.exists(innerDirectory2)) Files.createDirectory(innerDirectory2);
        if (!Files.exists(innerDirectory3)) Files.createDirectory(innerDirectory3);
    }

    @AfterEach
    void drop() throws IOException {
        Files.deleteIfExists(innerDirectory1);
        Files.deleteIfExists(innerDirectory2);
        Files.deleteIfExists(innerDirectory3);
        Files.deleteIfExists(testDirectory);
    }

    @Test
    void getFileList() throws IOException {
        Settings.write("", testDirectory.getFileName().toString());
        FileExplorer fileExplorer = new FileExplorer("leftPanel");
        fileExplorer.setCurrentDirectory(testDirectory);
        ObservableList<FileInfo> fileInfoObservableList = fileExplorer.getFileList();
        int size = fileInfoObservableList.size();
        Assertions.assertEquals(Files.list(testDirectory).count()+1, size);
    }

    @Test
    void setCurrentDirectory() {
        FileExplorer fileExplorer = new FileExplorer("leftPanel");
        Path testDirectory = Paths.get("test-directory");
        fileExplorer.setCurrentDirectory(testDirectory);
        String rightPanelCurrentDirectory = Settings.read("left-panel-current-directory");
        Assertions.assertEquals("test-directory", rightPanelCurrentDirectory);
    }
}