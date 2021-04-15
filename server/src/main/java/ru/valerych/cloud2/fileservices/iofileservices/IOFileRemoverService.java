package ru.valerych.cloud2.fileservices.iofileservices;

import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.fileservices.interfaces.FileRemover;
import ru.valerych.cloud2.fileservices.interfaces.FileRemoverService;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

import java.io.File;
import java.io.FileNotFoundException;

public class IOFileRemoverService implements FileRemoverService {

    private enum Stage {
        WAITING_FOR_FILE_NAME_LENGTH,
        WAITING_FOR_FILE_NAME,
        FILE_DELETE_PROCESS
    }

    private final Network network;
    private final ServerFileExplorer serverFileExplorer;

    private final Logger logger = LogManager.getLogger(IOFileRemoverService.class.getName());

    public IOFileRemoverService(Network network, ServerFileExplorer serverFileExplorer) {
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
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    logger.info("Length of file name '" + fileNameLength + "' was received");
                    stage = Stage.WAITING_FOR_FILE_NAME;
                    break;
                case WAITING_FOR_FILE_NAME:
                    fileName = new String(network.readBytesFromClient(fileNameLength));
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    logger.info("File name '" + fileName + "' was received");
                    stage = Stage.FILE_DELETE_PROCESS;
                    break;
                case FILE_DELETE_PROCESS:
                    String fileToDeletePathStr = serverFileExplorer.getCurrentDirectory().getPath() + Cloud2ServerSettings.FILE_SEPARATOR + fileName;
                    File fileToDelete = new File(fileToDeletePathStr);
                    FileRemover fileRemover = new IOFileRemover();
                    if (fileToDelete.exists()){
                        if (fileToDelete.isDirectory()){
                            fileRemover.deleteDirectory(fileToDeletePathStr);
                            logger.info(String.format("Directory '%s' was removed", fileToDeletePathStr));
                        } else {
                            fileRemover.deleteOneFile(fileToDeletePathStr);
                            logger.info(String.format("File '%s' was removed", fileToDeletePathStr));
                        }
                    } else {
                        logger.error(String.format("File '%s' isn't exist", fileToDelete.getAbsolutePath()));
                        throw new FileNotFoundException();
                    }
                    return;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }
        }
    }
}
