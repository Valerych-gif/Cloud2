package ru.valerych.cloud2.fileservices.iofileservices;

import org.junit.jupiter.api.*;
import ru.valerych.cloud2.entities.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class IOFileRemoverTest {


    private static final String USER_DIRECTORY = "../storage/0";
    private static final String USER_INNER_DIRECTORY = "/innerDirectoryForRemove";
    private static final String DIRECTORY_FOR_RECURSIVE_REMOVE = "/directoryForRecursiveRemove";
    private static final String USER_FILE = "/userFileToRemove.txt";

    private final IOFileRemover fileRemover = new IOFileRemover();

    @BeforeAll
    static void createFiles() throws IOException {
        User user = new User(0, "test", "test");
        user.setUpUser(Paths.get(USER_DIRECTORY));
    }

    @AfterAll
    static void clearRecourses(){
        File userFile = new File(USER_DIRECTORY + USER_FILE);
        if (userFile.exists()) {
            userFile.delete();
        }
        File directory = new File(USER_DIRECTORY + USER_INNER_DIRECTORY);
        if (directory.exists()) {
            directory.delete();
        }
        File userFile2 = new File(USER_DIRECTORY + DIRECTORY_FOR_RECURSIVE_REMOVE + USER_FILE);
        if (userFile2.exists()) {
            userFile2.delete();
        }
        File directory2 = new File(USER_DIRECTORY + USER_INNER_DIRECTORY);
        if (directory2.exists()) {
            directory2.delete();
        }
    }

    @Test
    @DisplayName("Deleting of one file is success")
    void deleteOneFileSuccessTest() throws IOException {
        File userFile = new File(USER_DIRECTORY + USER_FILE);
        if (!userFile.exists()) {
            userFile.createNewFile();
        }
        Assertions.assertTrue(userFile.exists());
        fileRemover.deleteOneFile(USER_DIRECTORY + USER_FILE);
        Assertions.assertFalse(userFile.exists());
    }

    @Test
    @DisplayName("Deleting of one file is fail (File not found)")
    void deleteOneFileFileNotFoundTest() throws IOException {
        File userFile = new File(USER_DIRECTORY + USER_FILE);
        if (userFile.exists()) {
            userFile.delete();
        }
        Assertions.assertThrows(FileNotFoundException.class, ()->fileRemover.deleteOneFile(USER_DIRECTORY + USER_FILE));
    }

    @Test
    @DisplayName("Deleting of empty directory is success")
    void deleteEmptyDirectoryTest() throws FileNotFoundException {
        File directory = new File(USER_DIRECTORY + USER_INNER_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
        Assertions.assertTrue(directory.exists());
        fileRemover.deleteDirectory(USER_DIRECTORY + USER_INNER_DIRECTORY);
        Assertions.assertFalse(directory.exists());
    }

    @Test
    @DisplayName("Deleting of empty directory is fail (Directory not found)")
    void deleteEmptyDirectoryNotFoundTest() throws IOException {
        File directory = new File(USER_DIRECTORY + USER_INNER_DIRECTORY);
        if (directory.exists()) {
            directory.delete();
        }
        Assertions.assertThrows(FileNotFoundException.class, ()->fileRemover.deleteDirectory(USER_DIRECTORY + USER_INNER_DIRECTORY));
    }

    @Test
    @DisplayName("Deleting of not empty directory is success")
    void deleteNotEmptyDirectoryTest() throws IOException {
        File directory = new File(USER_DIRECTORY + DIRECTORY_FOR_RECURSIVE_REMOVE);
        if (!directory.exists()) {
            directory.mkdir();
        }

        File userFile = new File(USER_DIRECTORY + DIRECTORY_FOR_RECURSIVE_REMOVE + USER_FILE);
        if (!userFile.exists()) {
            userFile.createNewFile();
        }
        Assertions.assertTrue(userFile.exists());
        fileRemover.deleteDirectory(USER_DIRECTORY + DIRECTORY_FOR_RECURSIVE_REMOVE);
        Assertions.assertFalse(directory.exists());
        Assertions.assertFalse(userFile.exists());
    }

    @Test
    @DisplayName("Deleting of not empty directory is fail (Directory not found)")
    void deleteNotEmptyDirectoryNotFoundTest() throws IOException {
        File userFile = new File(USER_DIRECTORY + DIRECTORY_FOR_RECURSIVE_REMOVE + USER_FILE);
        if (userFile.exists()) {
            userFile.delete();
        }
        File directory = new File(USER_DIRECTORY + USER_INNER_DIRECTORY);
        if (directory.exists()) {
            directory.delete();
        }
        Assertions.assertThrows(FileNotFoundException.class, ()->fileRemover.deleteDirectory(USER_DIRECTORY + USER_INNER_DIRECTORY));
    }
}