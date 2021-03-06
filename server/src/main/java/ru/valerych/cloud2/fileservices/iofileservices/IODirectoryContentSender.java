package ru.valerych.cloud2.fileservices.iofileservices;

import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.exceptions.IsNotDirectoryException;
import ru.valerych.cloud2.fileservices.interfaces.DirectoryContentSender;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IODirectoryContentSender implements DirectoryContentSender {

    private enum Stage {
        WAITING_FOR_DIR_NAME_LENGTH,
        WAITING_FOR_DIR_NAME,
        SEND_DIR_PROCESS
    }

    private final Network network;
    private final ServerFileExplorer serverFileExplorer;

    private final Logger logger = LogManager.getLogger(IODirectoryContentSender.class.getName());

    public IODirectoryContentSender(Network network, ServerFileExplorer serverFileExplorer) {
        this.network = network;
        this.serverFileExplorer = serverFileExplorer;
    }

    @Override
    public void sendDirectoryContent() {
        int dirNameLength = 0;
        String dirName = "";
        Stage stage = Stage.WAITING_FOR_DIR_NAME_LENGTH;
        boolean isDirectoryEnd = false;
        while (!isDirectoryEnd) {
            switch (stage) {
                case WAITING_FOR_DIR_NAME_LENGTH:
                    dirNameLength = network.readByteFromClient();
                    logger.info("Length of directory name '" + dirNameLength + "' was received");
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    stage = Stage.WAITING_FOR_DIR_NAME;
                    break;
                case WAITING_FOR_DIR_NAME:
                    dirName = new String(network.readBytesFromClient(dirNameLength));
                    logger.info("Directory name '" + dirName + "' was received");
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    stage = Stage.SEND_DIR_PROCESS;
                    break;
                case SEND_DIR_PROCESS:
                    try {
                        serverFileExplorer.goToDirectory(dirName);
                    } catch (IsNotDirectoryException | FileNotFoundException e) {
                        logger.error(e);
                    }
                    List<FileInfo> filesInfo = serverFileExplorer.getCurrentDirectoryContent();
                    for (FileInfo fileInfo : filesInfo) {
                        network.sendByteToClient(Responses.SEND_FILE_INFO.getSignalByte());
                        logger.info("Signal byte [" + Responses.SEND_FILE_INFO + "] was sent");

                        byte[] fileName = fileInfo.getFileName().getBytes(StandardCharsets.UTF_8);
                        byte fileNameLength = (byte) fileName.length;
                        byte type = fileInfo.getType().getMark();
                        long fileSize = fileInfo.getFileSize();

                        network.sendByteToClient(fileNameLength);
                        logger.info("File name length '" + fileNameLength + "' was sent");

                        network.sendBytesToClient(fileName);
                        logger.info("File name '" + new String(fileName) + "' was sent");

                        network.sendByteToClient(type);
                        logger.info("File type '" + type + "' was sent");

                        network.sendLongToClient(fileSize);
                        logger.info("File size '" + fileSize + "' was sent");
                    }
                    network.sendByteToClient(Responses.END_OF_DIR_CONTENT.getSignalByte());
                    stage = Stage.WAITING_FOR_DIR_NAME_LENGTH;
                    logger.info("Command 'End of directory sending' was sent");
                    isDirectoryEnd = true;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }
        }
    }
}
