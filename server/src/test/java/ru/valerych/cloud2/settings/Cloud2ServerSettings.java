package ru.valerych.cloud2.settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Cloud2ServerSettings {

    public static Properties properties;

    public final static String SERVER_MAIN_FILES_DIR;
    public final static String SERVER_TYPE;
    public final static int PORT;
    public final static String STORAGE_ROOT_DIR;
    public final static String AUTH_FILE;
    public final static String SHARED_FILE;
    public final static int BUFFER_SIZE;

    public final static String FILE_SEPARATOR = System.getProperty("file.separator");
    public final static String PARENT_DIR_MARK = "..";
    public final static String ROOT_DIR_MARK = ".";
    static {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/test/resources/application-test.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String tmp;
        tmp =properties.getProperty("server.main-files-dir");
        SERVER_MAIN_FILES_DIR = tmp!=null&&!tmp.trim().equals("")?tmp:"src/test/sfiles";
        SERVER_TYPE = "IOServer";
        PORT = 8189;
        STORAGE_ROOT_DIR = "storage";
        AUTH_FILE = "auth.db";
        SHARED_FILE = "sharedfiles.db";
        BUFFER_SIZE = 1024;
    }

}
