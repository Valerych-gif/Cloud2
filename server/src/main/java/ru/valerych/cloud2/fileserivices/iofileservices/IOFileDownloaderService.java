package ru.valerych.cloud2.fileserivices.iofileservices;

import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.fileserivices.interfaces.FileDownloaderService;
import ru.valerych.cloud2.fileserivices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

import java.io.File;
import java.nio.ByteBuffer;

public class IOFileDownloaderService implements FileDownloaderService {

    private enum Stage {
        WAITING_FOR_FILE_NAME_LENGTH,
        WAITING_FOR_FILE_NAME,
        SENDING_FILE_INFO,
        FILE_SEND_PROCESS
    }

    private final Network network;
    private final ServerFileExplorer serverFileExplorer;

    private final Logger logger = LogManager.getLogger(IOFileDownloaderService.class);

    public IOFileDownloaderService(Network network, ServerFileExplorer serverFileExplorer) {
        this.network = network;
        this.serverFileExplorer = serverFileExplorer;
    }

    @Override
    public void sendFileToClient() {
        int fileNameLength = 0;
        String fileName = "";
        FileInfo fileInfo = null;
        Stage stage = Stage.WAITING_FOR_FILE_NAME_LENGTH;
        while (true) {
            switch (stage) {
                case WAITING_FOR_FILE_NAME_LENGTH:
                    fileNameLength = network.readByteFromClient();
                    logger.info("Length of file name '" + fileNameLength + "' was received");
                    stage = Stage.WAITING_FOR_FILE_NAME;
                    break;
                case WAITING_FOR_FILE_NAME:
                    fileName = new String(network.readBytesFromClient(fileNameLength));
                    logger.info("File name '" + fileName + "' was received");
                    stage = Stage.SENDING_FILE_INFO;
                    break;
                case SENDING_FILE_INFO:
                    fileInfo = serverFileExplorer.getFileInfo(fileName);

                    byte type = fileInfo.getType().getMark();
                    network.sendByteToClient(type);
                    logger.info("File type '" + type + "' was sent");

                    byte[] fileSizeBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(fileInfo.getFileSize()).array();
                    network.sendBytesToClient(fileSizeBytes);
                    logger.info("File size '" + fileInfo.getFileSize() + "' was sent");
                    stage = Stage.FILE_SEND_PROCESS;

                    break;
                case FILE_SEND_PROCESS:
                    IOFileDownloader fileDownloader = new IOFileDownloader(new File(serverFileExplorer.getCurrentDirectory().getPath() + "/" + fileName));
                    byte[] buffer = new byte[Cloud2ServerSettings.BUFFER_SIZE];
                    int byteBufferSize = buffer.length;
                    long fileSize = fileInfo.getFileSize();

                    long numberOfParcels = fileSize / byteBufferSize;
                    int tailSize = (int) (fileSize - (numberOfParcels * byteBufferSize));

                    for (int i = 0; i < numberOfParcels; i++) {
                        fileDownloader.readBufferFromFile(buffer);
                        network.sendBufferToClient(buffer);
                    }
                    buffer = fileDownloader.readBytesFromFile(tailSize);
                    network.sendBufferToClient(buffer);
                    fileDownloader.closeFile();
                    return;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }
        }
    }
}
