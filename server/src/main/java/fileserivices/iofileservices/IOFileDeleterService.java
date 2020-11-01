package fileserivices.iofileservices;

import fileserivices.interfaces.FileDeleter;
import fileserivices.interfaces.FileDeleterService;
import fileserivices.interfaces.ServerFileExplorer;
import network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class IOFileDeleterService implements FileDeleterService {

    private enum Stage {
        WAITING_FOR_FILE_NAME_LENGTH,
        WAITING_FOR_FILE_NAME,
        FILE_DELETE_PROCESS
    }

    private final Network network;
    private final ServerFileExplorer serverFileExplorer;

    private final Logger logger = LogManager.getLogger(IOFileDeleterService.class);

    public IOFileDeleterService(Network network, ServerFileExplorer serverFileExplorer) {
        this.network = network;
        this.serverFileExplorer = serverFileExplorer;
    }

    public void deleteFile() throws FileNotFoundException {
        int fileNameLength = 0;
        String fileName = "";

        Stage stage = Stage.WAITING_FOR_FILE_NAME_LENGTH;
        while (true) {
            switch (stage) {
                case WAITING_FOR_FILE_NAME_LENGTH:
                    fileNameLength = network.readByteFromClient();
                    LogUtils.info("Length of file name '" + fileNameLength + "' was received", logger);
                    stage = Stage.WAITING_FOR_FILE_NAME;
                    break;
                case WAITING_FOR_FILE_NAME:
                    fileName = new String(network.readBytesFromClient(fileNameLength));
                    LogUtils.info("File name '" + fileName + "' was received", logger);
                    stage = Stage.FILE_DELETE_PROCESS;
                    break;
                case FILE_DELETE_PROCESS:
                    String fileToDeletePathStr = serverFileExplorer.getCurrentDirectory().getPath() + "/" + fileName;
                    File fileToDelete = new File(fileToDeletePathStr);
                    FileDeleter fileDeleter = new IOFileDeleter();
                    if (fileToDelete.exists()){
                        if (fileToDelete.isDirectory()){
                            fileDeleter.deleteDirectory(fileToDeletePathStr);
                        } else {
                            fileDeleter.deleteOneFile(fileToDeletePathStr);
                        }
                    } else {
                        throw new FileNotFoundException();
                    }
                    return;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }
        }
    }
}
