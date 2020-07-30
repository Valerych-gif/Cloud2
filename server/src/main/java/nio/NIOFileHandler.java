package nio;

import files.CloudFile;
import main.FileHandler;
import main.ConnectionHandler;

public class NIOFileHandler extends FileHandler {

    public NIOFileHandler(ConnectionHandler connectionHandler) {
        super(connectionHandler);
    }

    @Override
    public void init() {

    }

    @Override
    public boolean getFileFromStorage(CloudFile file) {
        return false;
    }

    @Override
    public boolean loadFileToStorage(CloudFile file) {
        return false;
    }
}
