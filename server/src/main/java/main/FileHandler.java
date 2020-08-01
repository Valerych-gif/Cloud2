package main;

import files.CloudFile;

public abstract class FileHandler {

    protected byte [] buffer;

    protected ConnectionHandler connectionHandler;
    protected int bufferSize;
    protected String storageRootDirPath;


    public FileHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.bufferSize = Cloud2ServerStarter.BUFFER_SIZE;
        this.buffer = new byte[bufferSize];
        this.storageRootDirPath = connectionHandler.getStorage().getAbsolutePath();
        init();
    }

    public abstract void init();

    public abstract boolean getFileFromStorage(CloudFile file);

    public abstract boolean loadFileToStorage(CloudFile file);
}
