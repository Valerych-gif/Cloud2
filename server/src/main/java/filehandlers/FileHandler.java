package filehandlers;

import authservice.UsersService;
import entities.CloudFile;
import network.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;

public abstract class FileHandler {

    protected Network network;

    protected Logger logger = LogManager.getLogger(FileHandler.class);

    public static final String DIR_PREFIX = "./D";
    public static final String FILE_PREFIX = "./F";
    public static final String PARENT_DIR_MARK = "..";

    protected byte [] buffer;

    protected UsersService usersService;
    protected int bufferSize;
    protected String storageRootDirPath;

    protected CloudFile currentStorageDir;
    protected CloudFile rootStorageDir;


    public FileHandler() {
        this.bufferSize = Cloud2ServerSettings.BUFFER_SIZE;
        this.buffer = new byte[bufferSize];
    }


    public abstract boolean getFileFromStorage(String file);

    public abstract boolean loadFileToStorage(CloudFile file);

    public abstract void deleteFileFromStorage(String fileName);
}
