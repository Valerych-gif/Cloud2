package ru.valerych.cloud2.settings;

public class Cloud2ServerSettings {
    public final static String SERVER_TYPE = "IOServer";
    public final static int PORT = 8189;

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public final static String STORAGE_ROOT_DIR = "storage";
    public final static String SERVER_MAIN_FILES_DIR = "./sfiles";
    public final static String AUTH_FILE = "auth.db";
    public final static String SHARED_FILE = "sharedfiles.db";

    public final static int BUFFER_SIZE = 1024;

    public final static String PARENT_DIR_MARK = "..";
    public final static String ROOT_DIR_MARK = ".";

    public final static boolean DEBUG_MODE_ENABLED = true;
}
