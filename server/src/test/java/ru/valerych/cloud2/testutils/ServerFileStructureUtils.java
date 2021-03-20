package ru.valerych.cloud2.testutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static ru.valerych.cloud2.fileservices.iofileservices.IOFileServicesConstants.*;
import static ru.valerych.cloud2.settings.Cloud2ServerSettings.BUFFER_SIZE;

public class ServerFileStructureUtils {

    public static File createUserDirectory() {
        File directory = new File(USER_DIRECTORY + FILE_SEPARATOR + USER_INNER_DIRECTORY);
        if (!directory.exists()) {
            if (!directory.mkdir())
                try {
                    throw new IOException(String.format("Can't to create file %s", directory.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return directory;
    }

    public static void removeUserDirectory() {
        File directory = new File(USER_DIRECTORY + FILE_SEPARATOR + USER_INNER_DIRECTORY);
        if (directory.exists()) {
            if (!directory.delete())
                try {
                    throw new IOException(String.format("Can't to remove directory %s", directory.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static File createUserDirectoryForRecursiveRemoving() {
        File directory = new File(USER_DIRECTORY + FILE_SEPARATOR + DIRECTORY_FOR_RECURSIVE_REMOVE);
        if (!directory.exists()) {
            if (!directory.mkdir())
                try {
                    throw new IOException(String.format("Can't to remove directory %s", directory.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return directory;
    }

    public static File createFileInDirectoryForRecursiveRemoving() {
        File userFile = new File(USER_DIRECTORY + FILE_SEPARATOR + DIRECTORY_FOR_RECURSIVE_REMOVE + FILE_SEPARATOR + USER_FILE);
        if (!userFile.exists()) {
            try {
                if (!userFile.createNewFile())
                    throw new IOException(String.format("Can't to create file %s", userFile.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userFile;
    }

    public static void removeUserDirectoryForRecursiveRemoving() {
        File userFile = new File(USER_DIRECTORY + FILE_SEPARATOR + DIRECTORY_FOR_RECURSIVE_REMOVE + FILE_SEPARATOR + USER_FILE);
        if (userFile.exists()) {
            if (!userFile.delete())
                try {
                    throw new IOException(String.format("Can't to remove file %s", userFile.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        File directory = new File(USER_DIRECTORY + FILE_SEPARATOR + USER_INNER_DIRECTORY);
        if (directory.exists()) {
            if (!userFile.delete())
                try {
                    throw new IOException(String.format("Can't to remove directory %s", directory.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static File createUserFile() {
        File userFile = new File(USER_DIRECTORY + FILE_SEPARATOR + USER_FILE);
        if (!userFile.exists()) {
            try {
                if (!userFile.createNewFile())
                    throw new IOException(String.format("Can't to create file %s", userFile.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return userFile;
    }

    public static File createFilledUserFile() {
        File userFile = new File(USER_DIRECTORY + FILE_SEPARATOR + USER_FILE);
        removeUserFile();
        createUserFile();
        try (OutputStream os = new FileOutputStream(userFile)) {
            String testLine = "Test line for writing to userFile.txt\n";
            for (int i = 0; i < BUFFER_SIZE / testLine.length(); i++) {
                os.write(testLine.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userFile;
    }

    public static void removeUserFile() {
        File userFile = new File(USER_DIRECTORY + FILE_SEPARATOR + USER_FILE);
        if (userFile.exists()) {
            try {
                if (!userFile.delete())
                    throw new IOException(String.format("Can't to remove file %s", userFile.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeFileStructure() {
        removeUserFile();
        removeUserDirectory();
        removeUserDirectoryForRecursiveRemoving();
    }
}
