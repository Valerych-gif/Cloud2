package ru.valerych.cloud2.client.services.fileservices;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.valerych.cloud2.client.entities.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static ru.valerych.cloud2.client.utils.FilesStructure.*;

class LocalFileExplorerTest {

    private final Path rootDirectory = ROOT_DIRECTORY;
    private final Path testDirectory = TEST_DIRECTORY;

    @BeforeEach
    void init() {
        create();
    }

    @AfterEach
    void drop() {
        delete();
    }

    @Test
    void getFileListInSimpleDirectory() throws IOException {
        LocalFileExplorer localFileExplorer = new LocalFileExplorer("Cloud2Directory");
        localFileExplorer.setCurrentDirectory(testDirectory.getFileName().toString());
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
        LocalFileExplorer localFileExplorer = new LocalFileExplorer("Cloud2Directory");
        ObservableList<FileInfo> fileInfoObservableList = localFileExplorer.getFileList();
        List<String> fileNames = fileInfoObservableList.stream().map(FileInfo::getFileName).collect(Collectors.toList());
        int size = fileInfoObservableList.size();
        Assertions.assertEquals(Files.list(rootDirectory).count(), size);
        Assertions.assertTrue(fileNames.contains(testDirectory.getFileName().toString()));
    }

    @Test
    void setCurrentDirectory() {
        LocalFileExplorer localFileExplorer = new LocalFileExplorer("Cloud2Directory");
        localFileExplorer.setCurrentDirectory("test-directory");
        Assertions.assertEquals(Paths.get("Cloud2Directory","test-directory"), localFileExplorer.getCurrentDirectory());
    }
}