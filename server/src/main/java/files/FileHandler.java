package files;

import main.Cloud2ServerStarter;
import main.ConnectionHandler;

import java.io.IOException;

public abstract class FileHandler {

    protected byte [] buffer;

    protected ConnectionHandler connectionHandler;
    protected int bufferSize;
    protected String storageRootDirPath;


    public FileHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.buffer = new byte[Cloud2ServerStarter.BUFFER_SIZE];
        this.bufferSize = Cloud2ServerStarter.BUFFER_SIZE;
        this.storageRootDirPath = connectionHandler.getStorage().getAbsolutePath();
        init();
    }

    public abstract void init();

    public abstract boolean getFileFromStorage(CloudFile file);

    public abstract boolean loadFileToStorage(CloudFile file);
}
