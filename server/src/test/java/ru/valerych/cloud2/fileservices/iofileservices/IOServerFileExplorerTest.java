package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.IsNotDirectoryException;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.USER_DIRECTORY;
import static ru.valerych.cloud2.settings.Cloud2ServerSettings.FILE_SEPARATOR;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.*;

class IOServerFileExplorerTest {

    private static User user;

    @BeforeAll
    static void setUpUser(){
        user = new User(0, "test", "test");
        user.setUpUser();
    }

    @AfterAll
    static void deleteDirectories(){
        removeFileStructure();
    }

    @Test
    @DisplayName("Going to directory is success")
    void goToDirectorySuccessTest() throws IsNotDirectoryException, FileNotFoundException {
        File directory = createUserDirectory();
        ServerFileExplorer fileExplorer = new IOServerFileExplorer(user);
        fileExplorer.goToDirectory("");
        File currentDirectory = fileExplorer.getCurrentDirectory();
        Assertions.assertEquals(new File(USER_DIRECTORY), currentDirectory);
        fileExplorer.goToDirectory(directory.getName());
        currentDirectory = fileExplorer.getCurrentDirectory();
        Assertions.assertEquals(new File(USER_DIRECTORY + FILE_SEPARATOR + directory.getName()), currentDirectory);

    }

    @Test
    @DisplayName("Going to directory is fail. FileNotFoundException")
    void goToDirectoryFileNotFoundException() {
        File directory = new File("someDir");
        ServerFileExplorer fileExplorer = new IOServerFileExplorer(user);
        Assertions.assertThrows(FileNotFoundException.class, ()->fileExplorer.goToDirectory(directory.getName()));
    }

    @Test
    @DisplayName("Going to directory is fail. IsNotDirectoryException")
    void goToDirectoryIsNotDirectoryException() {
        createUserDirectory();
        File file = createUserFile();
        ServerFileExplorer fileExplorer = new IOServerFileExplorer(user);
        Assertions.assertThrows(IsNotDirectoryException.class, ()->fileExplorer.goToDirectory(file.getName()));
    }

    @Test
    @DisplayName("Getting current directory content is success")
    void getCurrentDirectoryContentSuccessTest() {
        File directory = createUserDirectory();
        File file = createUserFile();
        ServerFileExplorer fileExplorer = new IOServerFileExplorer(user);
        List<FileInfo> fileInfoList = fileExplorer.getCurrentDirectoryContent();
        List<String> fileNames = fileInfoList.stream().map(FileInfo::getFileName).collect(Collectors.toList());
        Assertions.assertTrue(fileNames.contains(directory.getName()));
        Assertions.assertTrue(fileNames.contains(file.getName()));
    }

    @Test
    @DisplayName("Getting file info is success")
    void getFileInfoSuccessTest() {
        File file = createUserFile();
        ServerFileExplorer fileExplorer = new IOServerFileExplorer(user);
        FileInfo fileInfo = fileExplorer.getFileInfo(file.getName());
        Assertions.assertEquals(fileInfo.getFileName(), file.getName());
        Assertions.assertEquals(fileInfo.getFileSize(), file.length());
        Assertions.assertEquals(fileInfo.getType(), FileInfo.Type.FILE);
    }
}