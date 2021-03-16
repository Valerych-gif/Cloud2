package ru.valerych.cloud2.fileservices.iofileservices;

public class IOFileServicesConstants {
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String USER_DIRECTORY = ".." + FILE_SEPARATOR + "storage" + FILE_SEPARATOR + "0";
    public static final String USER_INNER_DIRECTORY = FILE_SEPARATOR + "innerDirectoryForRemove";
    public static final String DIRECTORY_FOR_RECURSIVE_REMOVE = FILE_SEPARATOR + "directoryForRecursiveRemove";
    public static final String USER_FILE = "userFileToRemove.txt";
}
