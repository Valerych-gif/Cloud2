package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static ru.valerych.cloud2.utils.ServerFileStructureUtils.*;

class IOServerFileExplorerTest {

    private static User user;

    @BeforeAll
    static void setUpUser(){
        user = new User(0, "test", "test");
        user.setUpUser(Paths.get("../storage/0"));
    }

    @AfterAll
    static void deleteDirectories(){
        removeFileStructure();
    }

    @Test
    @DisplayName("Going to directory is success")
    void goToDirectorySuccessTest() {
        File directory = createUserDirectory();
        ServerFileExplorer fileExplorer = new IOServerFileExplorer(user);
        fileExplorer.goToDirectory("");
        File currentDirectory = fileExplorer.getCurrentDirectory();
        Assertions.assertEquals(new File("../storage/0/"), currentDirectory);
        fileExplorer.goToDirectory(directory.getName());
        currentDirectory = fileExplorer.getCurrentDirectory();
        Assertions.assertEquals(new File("../storage/0/" + directory.getName()), currentDirectory);

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