package fileserivices.iofileservices;

import entities.FileInfo;
import fileserivices.interfaces.FileDownloaderService;
import fileserivices.interfaces.ServerFileExplorer;
import network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import utils.LogUtils;

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
                    LogUtils.info("Length of file name '" + fileNameLength + "' was received", logger);
                    stage = Stage.WAITING_FOR_FILE_NAME;
                    break;
                case WAITING_FOR_FILE_NAME:
                    fileName = new String(network.readBytesFromClient(fileNameLength));
                    LogUtils.info("File name '" + fileName + "' was received", logger);
                    stage = Stage.SENDING_FILE_INFO;
                    break;
                case SENDING_FILE_INFO:
                    fileInfo = serverFileExplorer.getFileInfo(fileName);

                    byte type = fileInfo.getType().getMark();
                    network.sendByteToClient(type);
                    LogUtils.info("File type '" + type + "' was sent", logger);

                    byte[] fileSizeBytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(fileInfo.getFileSize()).array();
                    network.sendBytesToClient(fileSizeBytes);
                    LogUtils.info("File size '" + fileInfo.getFileSize() + "' was sent", logger);
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
