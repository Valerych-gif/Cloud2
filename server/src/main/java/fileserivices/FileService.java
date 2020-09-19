package fileserivices;

import commands.Responses;
import entities.FileInfo;
import entities.User;
import network.IONetwork;
import network.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import utils.LogUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

public class FileService {

    public final static String PARENT_DIR_MARK = "..";
    public final static String ROOT_DIR_MARK = ".";

    private enum Stage {
        WAITING_FOR_DIR_NAME_LENGTH,
        WAITING_FOR_DIR_NAME,
        SEND_DIR_PROCESS,
        WAITING_FOR_FILE_NAME_LENGTH,
        WAITING_FOR_FILE_NAME,
        WAITING_FOR_FILE_SIZE,
        FILE_RECEIVE_PROCESS,
        FILE_SEND_PROCESS
    }

    private Stage stage;

    private byte[] buffer;
    private int bufferSize;

    private ServerFileExplorer serverFileExplorer;

    private Network network;
    private User user;

    private Logger logger = LogManager.getLogger(FileService.class);

    public FileService(User user, Network network) {
        if (network instanceof IONetwork) {
            this.serverFileExplorer = new IOServerFileExplorer(user);
            this.network = network;
            this.user = user;
        }
        this.bufferSize = Cloud2ServerSettings.BUFFER_SIZE;
        this.buffer = new byte[bufferSize];
    }

    public void sendDirContent() {
        int dirNameLength = 0;
        String dirName = "";
        stage = Stage.WAITING_FOR_DIR_NAME_LENGTH;
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

    public boolean receiveFileFromClient() {
        int fileNameLength = 0;
        long fileSize = 0;
        String fileName = "";
        stage = Stage.WAITING_FOR_FILE_NAME_LENGTH;
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
                    stage = Stage.WAITING_FOR_FILE_SIZE;
                    break;
                case WAITING_FOR_FILE_SIZE:
                    ByteBuffer byteBuffer = ByteBuffer
                            .allocate(Long.BYTES)
                            .put(network.readBytesFromClient(8));
                    byteBuffer.flip();
                    fileSize = byteBuffer.getLong();
                    LogUtils.info("File size '" + fileSize + "' was received", logger);
                    stage = Stage.FILE_RECEIVE_PROCESS;
                    break;
                case FILE_RECEIVE_PROCESS:
                    int byteBufferSize = Cloud2ServerSettings.BUFFER_SIZE;
                    buffer = new byte[byteBufferSize];

                    long numberOfParcels = fileSize/byteBufferSize;
                    int tailSize = (int)(fileSize - (numberOfParcels*byteBufferSize));

                    File fileToUpload = new File(serverFileExplorer.getCurrentDirectory().getPath() + "/" + fileName);
                    IOFileUploader fileUploader = new IOFileUploader(fileToUpload);

                    for (int i = 0; i < numberOfParcels; i++) {
                        network.readBufferFromClient(buffer);
                        fileUploader.writeBufferToFile(buffer);
                        System.out.print(new String(buffer));
                    }

                    buffer = network.readBytesFromClient(tailSize);
                    fileUploader.writeBufferToFile(buffer);
                    System.out.print(new String(buffer));
                    fileUploader.closeFile();
                    stage = Stage.WAITING_FOR_DIR_NAME_LENGTH;
                    return true;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }

        }
    }
}
