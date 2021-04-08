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
import java.util.List;
import java.util.stream.Collectors;

class LocalFileExplorerTest {

    Path rootDirectory = Paths.get("./Cloud2Directory");
    Path testDirectory = Paths.get("./Cloud2Directory", "testDirectory");
    Path innerDirectory1 = Paths.get("./Cloud2Directory", "testDirectory", "innerDirectory1");
    Path innerDirectory2 = Paths.get("./Cloud2Directory", "testDirectory", "innerDirectory2");
    Path innerDirectory3 = Paths.get("./Cloud2Directory", "testDirectory", "innerDirectory3");

    @BeforeEach
    void init() throws IOException {
        if (!Files.exists(rootDirectory)) Files.createDirectory(rootDirectory);
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
        Files.deleteIfExists(rootDirectory);
    }

    @Test
    void getFileListInSimpleDirectory() throws IOException {
        Settings.write("left-panel-current-directory", testDirectory.getFileName().toString());
        LocalFileExplorer localFileExplorer = new LocalFileExplorer("leftPanel");
        localFileExplorer.setCurrentDirectory(testDirectory);
        ObservableList<FileInfo> fileInfoObservableList = localFileExplorer.getFileList();
        List<String> fileNames = fileInfoObservableList.stream().map(FileInfo::getFileName).collect(Collectors.toList());
        int size = fileInfoObservableList.size();
        Assertions.assertEquals(Files.list(testDirectory).count()+1, size);
        Assertions.assertTrue(fileNames.contains(".."));
        Assertions.assertTrue(fileNames.contains("innerDirectory1"));
        Assertions.assertTrue(fileNames.contains("innerDirectory2"));
        Assertions.assertTrue(fileNames.contains("innerDirectory3"));
    }

    @Test
    void getFileListInRootDirectory() throws IOException {
        Settings.write("left-panel-current-directory", "./Cloud2Directory");
        LocalFileExplorer localFileExplorer = new LocalFileExplorer("leftPanel");
        ObservableList<FileInfo> fileInfoObservableList = localFileExplorer.getFileList();
        List<String> fileNames = fileInfoObservableList.stream().map(FileInfo::getFileName).collect(Collectors.toList());
        int size = fileInfoObservableList.size();
        Assertions.assertEquals(Files.list(rootDirectory).count(), size);
        Assertions.assertTrue(fileNames.contains(testDirectory.getFileName().toString()));
    }

    @Test
    void setCurrentDirectory() {
        LocalFileExplorer localFileExplorer = new LocalFileExplorer("leftPanel");
        Path testDirectory = Paths.get("test-directory");
        localFileExplorer.setCurrentDirectory(testDirectory);
        String rightPanelCurrentDirectory = Settings.read("left-panel-current-directory");
        Assertions.assertEquals("test-directory", rightPanelCurrentDirectory);
    }
}