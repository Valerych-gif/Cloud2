package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.iofileservices.IODirectoryContentSender;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.shareservice.interfaces.SharedFilesDirectoryContentSender;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.valerych.cloud2.settings.Cloud2ServerSettings.*;

public class IOSharedFilesDirectoryContentSender implements SharedFilesDirectoryContentSender {

    private final Logger logger = LogManager.getLogger(IOSharedFilesDirectoryContentSender.class.getName());
    private final User user;
    private final Network network;

    public IOSharedFilesDirectoryContentSender(User user, Network network) {
        this.user = user;
        this.network = network;
    }

    @Override
    public void sendSharedFilesDirectoryContent() throws FileNotFoundException {
        List<FileInfo> filesInfo = new ArrayList<>();
        File sharedFilesDb = new File(SERVER_MAIN_FILES_DIR + FILE_SEPARATOR + SHARED_FILE);
        if (!sharedFilesDb.exists()) throw new FileNotFoundException(String.format("File with shared files DB '%s' isn't exists", sharedFilesDb.getAbsolutePath()));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(SERVER_MAIN_FILES_DIR + FILE_SEPARATOR + SHARED_FILE)))) {
            filesInfo = reader.lines()
                    .map(s -> s.split(" "))
                    .filter(s -> (user.getId() == Integer.parseInt(s[1])))
                    .filter(s -> {
                        File file = new File(s[2]);
                        return file.exists();
                    })
                    .map(s -> {
                        String fileName = s[2];
                        File file = new File(fileName);
                        return new FileInfo(fileName, file.length(), FileInfo.Type.FILE);
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(e);
        }
        sendFileListToClient(filesInfo);
    }

    private void sendFileListToClient(List<FileInfo> filesInfo) {
        for (FileInfo fileInfo : filesInfo) {

            network.sendByteToClient(Responses.SEND_FILE_INFO.getSignalByte());
            logger.info("Signal byte [" + Responses.SEND_FILE_INFO + "] was sent");

            byte[] fileName = fileInfo.getFileName().getBytes(StandardCharsets.UTF_8);
            byte fileNameLength = (byte) fileName.length;
            network.sendByteToClient(fileNameLength);
            logger.info("File name length '" + fileNameLength + "' was sent");

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
