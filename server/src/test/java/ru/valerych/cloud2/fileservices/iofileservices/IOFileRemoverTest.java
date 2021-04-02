package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.entities.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.*;
import static ru.valerych.cloud2.settings.Cloud2ServerSettings.FILE_SEPARATOR;
import static ru.valerych.cloud2.utils.ServerFileStructureUtils.*;

class IOFileRemoverTest {

    private final IOFileRemover fileRemover = new IOFileRemover();

    @BeforeAll
    static void initUserDirectory() {
        User user = new User(0, "test", "test");
        user.setUpUser();
    }

    @AfterAll
    static void clearRecourses(){
        removeFileStructure();
    }

    @Test
    @DisplayName("Deleting of one file is success")
    void deleteOneFileSuccessTest() throws IOException {
        File userFile = createUserFile();
        Assertions.assertTrue(userFile.exists());
        fileRemover.deleteOneFile(USER_DIRECTORY + FILE_SEPARATOR + USER_FILE);
        Assertions.assertFalse(userFile.exists());
    }

    @Test
    @DisplayName("Deleting of one file is fail (File not found)")
    void deleteOneFileFileNotFoundTest() {
        removeUserFile();
        Assertions.assertThrows(FileNotFoundException.class, ()->fileRemover.deleteOneFile(USER_DIRECTORY + FILE_SEPARATOR + USER_FILE));
    }

    @Test
    @DisplayName("Deleting of empty directory is success")
    void deleteEmptyDirectoryTest() throws FileNotFoundException {
        File directory = createUserDirectory();
        Assertions.assertTrue(directory.exists());
        fileRemover.deleteDirectory(USER_DIRECTORY + FILE_SEPARATOR + USER_INNER_DIRECTORY);
        Assertions.assertFalse(directory.exists());
    }

    @Test
    @DisplayName("Deleting of empty directory is fail (Directory not found)")
    void deleteEmptyDirectoryNotFoundTest() {
        removeUserDirectory();
        Assertions.assertThrows(FileNotFoundException.class, ()->fileRemover.deleteDirectory(USER_DIRECTORY + FILE_SEPARATOR + USER_INNER_DIRECTORY));
    }

    @Test
    @DisplayName("Deleting of not empty directory is success")
    void deleteNotEmptyDirectoryTest() throws FileNotFoundException {
        File directory = createUserDirectoryForRecursiveRemoving();
        File userFile = createFileInDirectoryForRecursiveRemoving();
        Assertions.assertTrue(userFile.exists());
        Assertions.assertTrue(directory.exists());
        fileRemover.deleteDirectory(USER_DIRECTORY + FILE_SEPARATOR + DIRECTORY_FOR_RECURSIVE_REMOVE);
        Assertions.assertFalse(directory.exists());
        Assertions.assertFalse(userFile.exists());
    }

    @Test
    @DisplayName("Deleting of not empty directory is fail (Directory not found)")
    void deleteNotEmptyDirectoryNotFoundTest() {
        removeUserDirectoryForRecursiveRemoving();
        Assertions.assertThrows(FileNotFoundException.class, ()->fileRemover.deleteDirectory(USER_DIRECTORY + FILE_SEPARATOR + USER_INNER_DIRECTORY));
    }
}