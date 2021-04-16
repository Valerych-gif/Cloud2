package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.FileDownloaderService;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.shareservice.interfaces.SharedFileDownloader;
import ru.valerych.cloud2.shareservice.interfaces.SharedFileDownloaderSender;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static ru.valerych.cloud2.settings.Cloud2ServerSettings.*;

public class IOSharedFileDownloaderService implements SharedFileDownloaderSender {

    private enum Stage {
        WAITING_FOR_FILE_NAME_LENGTH,
        WAITING_FOR_FILE_NAME,
        SENDING_FILE_INFO,
        FILE_SEND_PROCESS
    }

    private final Network network;
    private final User user;

    private final Logger logger = LogManager.getLogger(IOSharedFileDownloaderService.class.getName());

    public IOSharedFileDownloaderService(Network network, User user) {
        this.network = network;
        this.user = user;
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
                    fileInfo = checkUserRights(fileName);
                    if (fileInfo==null) {
                        network.sendByteToClient(Responses.FAIL.getSignalByte());
                        return;
                    }

                    byte type = fileInfo.getType().getMark();
                    network.sendByteToClient(type);
                    logger.info("File type '" + type + "' was sent");

                    network.sendLongToClient(fileInfo.getFileSize());
                    logger.info("File size '" + fileInfo.getFileSize() + "' was sent");
                    stage = Stage.FILE_SEND_PROCESS;

                    break;
                case FILE_SEND_PROCESS:
                    sendingFileToClient(fileName, fileInfo);
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    return;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }
        }
    }

    private void sendingFileToClient(String fileName, FileInfo fileInfo) {
        SharedFileDownloader fileDownloader = new IOSharedFileDownloader(new File(fileName));
        byte[] buffer = new byte[Cloud2ServerSettings.BUFFER_SIZE];
        int byteBufferSize = buffer.length;
        long fileSize = fileInfo.getFileSize();

        long numberOfParcels = fileSize / byteBufferSize;
        int tailSize = (int) (fileSize - (numberOfParcels * byteBufferSize));
        try {
            for (int i = 0; i < numberOfParcels; i++) {
                fileDownloader.readBufferFromFile(buffer);
                network.sendBufferToClient(buffer);
            }
        } catch (IOException e) {
            logger.error(e);
            return;
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

    private FileInfo checkUserRights(String fileName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(SERVER_MAIN_FILES_DIR + FILE_SEPARATOR + SHARED_FILE)))) {
            return reader.lines()
                    .map(s -> s.split(" "))
                    .filter(s -> (user.getId() == Integer.parseInt(s[1])))
                    .filter(s -> {
                        if (!fileName.equals(s[2])) return false;
                        File file = new File(s[2]);
                        return file.exists();
                    })
                    .map(s -> {
                        String fName = s[2];
                        File file = new File(fName);
                        return new FileInfo(fName, file.length(), FileInfo.Type.FILE);
                    })
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            logger.error(e);
        }
        return null;
    }
}
