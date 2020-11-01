package fileserivices.iofileservices;

import commands.Responses;
import entities.FileInfo;
import fileserivices.interfaces.DirectoryContentSender;
import fileserivices.interfaces.ServerFileExplorer;
import network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.nio.ByteBuffer;
import java.util.List;

public class IODirectoryContentSender implements DirectoryContentSender {

    private enum Stage {
        WAITING_FOR_DIR_NAME_LENGTH,
        WAITING_FOR_DIR_NAME,
        SEND_DIR_PROCESS
    }

    private final Network network;
    private final ServerFileExplorer serverFileExplorer;

    private final Logger logger = LogManager.getLogger(IODirectoryContentSender.class);

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
                    LogUtils.info("Length of directory name '" + dirNameLength + "' was received", logger);
                    stage = Stage.WAITING_FOR_DIR_NAME;
                    break;
                case WAITING_FOR_DIR_NAME:
                    dirName = new String(network.readBytesFromClient(dirNameLength));
                    LogUtils.info("Directory name '" + dirName + "' was received", logger);
                    stage = Stage.SEND_DIR_PROCESS;
                    break;
                case SEND_DIR_PROCESS:
                    serverFileExplorer.goToDirectory(dirName);
                    List<FileInfo> filesInfo = serverFileExplorer.getCurrentDirectoryContent();
                    for (FileInfo fileInfo : filesInfo) {
                        byte fileNameLength = (byte) fileInfo.getFileName().length();
                        network.sendByteToClient(fileNameLength);
                        LogUtils.info("File name length '" + fileNameLength + "' was sent", logger);

                        byte[] fileName = fileInfo.getFileName().getBytes();
                        network.sendBytesToClient(fileName);
                        LogUtils.info("File name '" + new String(fileName) + "' was sent", logger);

                        byte type = fileInfo.getType().getMark();
                        network.sendByteToClient(type);
                        LogUtils.info("File type '" + type + "' was sent", logger);

                        byte[] fileSize = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(fileInfo.getFileSize()).array();
                        network.sendBytesToClient(fileSize);
                        LogUtils.info("File size '" + fileInfo.getFileSize() + "' was sent", logger);
                    }
                    network.sendByteToClient(Responses.END_OF_DIR_CONTENT.getSignalByte());
                    stage = Stage.WAITING_FOR_DIR_NAME_LENGTH;
                    LogUtils.info("Command 'End of directory sending' was sent", logger);
                    isDirectoryEnd = true;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }
        }
    }
}
