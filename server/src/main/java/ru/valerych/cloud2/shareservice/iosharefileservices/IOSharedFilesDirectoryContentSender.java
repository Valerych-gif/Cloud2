package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.iofileservices.IODirectoryContentSender;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.shareservice.interfaces.SharedFilesDirectoryContentSender;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class IOSharedFilesDirectoryContentSender implements SharedFilesDirectoryContentSender {

    private final Logger logger = LogManager.getLogger(IOSharedFilesDirectoryContentSender.class.getName());
    private User user;
    private Network network;

    public IOSharedFilesDirectoryContentSender(User user, Network network) {
        this.user = user;
        this.network = network;
    }

    @Override
    public void sendSharedFilesDirectoryContent() {
        List<FileInfo> filesInfo = new ArrayList<>();

        for (FileInfo fileInfo : filesInfo) {
            byte fileNameLength = (byte) fileInfo.getFileName().length();
            network.sendByteToClient(fileNameLength);
            logger.info("File name length '" + fileNameLength + "' was sent");

            byte[] fileName = fileInfo.getFileName().getBytes();
            network.sendBytesToClient(fileName);
            logger.info("File name '" + new String(fileName) + "' was sent");

            byte type = fileInfo.getType().getMark();
            network.sendByteToClient(type);
            logger.info("File type '" + type + "' was sent");

            byte[] fileSize = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(fileInfo.getFileSize()).array();
            network.sendBytesToClient(fileSize);
            logger.info("File size '" + fileInfo.getFileSize() + "' was sent");
        }
        network.sendByteToClient(Responses.END_OF_DIR_CONTENT.getSignalByte());
    }

}
