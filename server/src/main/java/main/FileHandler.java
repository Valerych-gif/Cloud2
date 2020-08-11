package main;

import auth.AuthService;
import files.CloudFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class FileHandler {

    protected Logger logger = LogManager.getLogger(FileHandler.class);

    public static final String DIR_PREFIX = "./D";
    public static final String FILE_PREFIX = "./F";
    public static final String PARENT_DIR_MARK = "..";

    protected byte [] buffer;

    protected ConnectionHandler connectionHandler;
    protected AuthService authService;
    protected int bufferSize;
    protected String storageRootDirPath;

    protected CloudFile currentStorageDir;
    protected CloudFile rootStorageDir;


    public FileHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.authService = AuthService.getInstance();
        this.bufferSize = Cloud2ServerStarter.BUFFER_SIZE;
        this.buffer = new byte[bufferSize];
        this.storageRootDirPath = connectionHandler.getUserStorage().getAbsolutePath();
        this.rootStorageDir = new CloudFile(storageRootDirPath);
        this.currentStorageDir = rootStorageDir;
        init();
    }

    public abstract void init();

    public abstract boolean getFileFromStorage(String file);

    public abstract boolean loadFileToStorage(CloudFile file);

    public abstract void sendDirContentToClient();

    public abstract void sendSharedFilesToClient();
}
