package ru.valerych.cloud2.fileservices.iofileservices;

import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.fileservices.interfaces.FileDownloaderService;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class IOFileDownloaderService implements FileDownloaderService {

    private enum Stage {
        WAITING_FOR_FILE_NAME_LENGTH,
        WAITING_FOR_FILE_NAME,
        SENDING_FILE_INFO,
        FILE_SEND_PROCESS
    }

    private final Network network;
    private final ServerFileExplorer serverFileExplorer;

    private final Logger logger = LogManager.getLogger(IOFileDownloaderService.class.getName());

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
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    stage = Stage.WAITING_FOR_FILE_NAME;
                    break;
                case WAITING_FOR_FILE_NAME:
                    fileName = new String(network.readBytesFromClient(fileNameLength));
                    logger.info("File name '" + fileName + "' was received");
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    stage = Stage.SENDING_FILE_INFO;
                    break;
                case SENDING_FILE_INFO:
                    fileInfo = serverFileExplorer.getFileInfo(fileName);

                    byte type = fileInfo.getType().getMark();
                    network.sendByteToClient(type);
                    logger.info("File type '" + type + "' was sent");

                    network.sendLongToClient(fileInfo.getFileSize());
                    logger.info("File size '" + fileInfo.getFileSize() + "' was sent");
                    stage = Stage.FILE_SEND_PROCESS;

                    break;
                case FILE_SEND_PROCESS:
                    sendingFileToClient(fileName, fileInfo);
                    return;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }
        }
    }

    private void sendingFileToClient(String fileName, FileInfo fileInfo) {
        IOFileDownloader fileDownloader = new IOFileDownloader(new File(serverFileExplorer.getCurrentDirectory().getPath() + "/" + fileName));
        byte[] buffer = new byte[Cloud2ServerSettings.BUFFER_SIZE];
        int byteBufferSize = buffer.length;
        long fileSize = fileInfo.getFileSize();

        long numberOfParcels = fileSize / byteBufferSize;
        int tailSize = (int) (fileSize - (numberOfParcels * byteBufferSize));

        for (int i = 0; i < numberOfParcels; i++) {
            try {
                fileDownloader.readBufferFromFile(buffer);
            } catch (IOException e) {
                logger.error(e);
                return;
            }
            network.sendBufferToClient(buffer);
        }
        try {
            buffer = fileDownloader.readBytesFromFile(tailSize);
        } catch (IOException e) {
            logger.error(e);
            return;
        }
        network.sendBufferToClient(buffer);
        fileDownloader.closeFile();
    }

    private byte[] longToByteArray(long value) {
        return new byte[] {
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    private long byteArrayToLong(byte[] bytes) {
        return ((long) (bytes[0]) << 56)
                + (((long) bytes[1] & 0xFF) << 48)
                + ((long) (bytes[2] & 0xFF) << 40)
                + ((long) (bytes[3] & 0xFF) << 32)
                + ((long) (bytes[4] & 0xFF) << 24)
                + ((bytes[5] & 0xFF) << 16)
                + ((bytes[6] & 0xFF) << 8)
                + (bytes[7] & 0xFF);
    }
}
