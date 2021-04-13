package ru.valerych.cloud2.client.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesStructure {

    public static Path ROOT_DIRECTORY = Paths.get("./Cloud2Directory");
    public static Path TEST_DIRECTORY = Paths.get("./Cloud2Directory", "testDirectory");
    public static Path INNER_DIRECTORY_1 = Paths.get("./Cloud2Directory", "testDirectory", "innerDirectory1");
    public static Path INNER_DIRECTORY_2 = Paths.get("./Cloud2Directory", "testDirectory", "innerDirectory2");
    public static Path INNER_DIRECTORY_3 = Paths.get("./Cloud2Directory", "testDirectory", "innerDirectory3");


    public static void create(){
        if (!Files.exists(ROOT_DIRECTORY)) {
            try {
                Files.createDirectory(ROOT_DIRECTORY);
                if (!Files.exists(TEST_DIRECTORY)) Files.createDirectory(TEST_DIRECTORY);
                if (!Files.exists(INNER_DIRECTORY_1)) Files.createDirectory(INNER_DIRECTORY_1);
                if (!Files.exists(INNER_DIRECTORY_2)) Files.createDirectory(INNER_DIRECTORY_2);
                if (!Files.exists(INNER_DIRECTORY_3)) Files.createDirectory(INNER_DIRECTORY_3);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void delete(){
        try {
            Files.deleteIfExists(INNER_DIRECTORY_1);
            Files.deleteIfExists(INNER_DIRECTORY_2);
            Files.deleteIfExists(INNER_DIRECTORY_3);
            Files.deleteIfExists(TEST_DIRECTORY);
            Files.deleteIfExists(ROOT_DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
