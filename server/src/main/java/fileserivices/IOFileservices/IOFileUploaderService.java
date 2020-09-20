package fileserivices.IOFileservices;

import fileserivices.interfaces.FileUploaderService;
import fileserivices.interfaces.ServerFileExplorer;
import network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import utils.LogUtils;

import java.io.File;
import java.nio.ByteBuffer;

public class IOFileUploaderService implements FileUploaderService {

    private enum Stage {
        WAITING_FOR_FILE_NAME_LENGTH,
        WAITING_FOR_FILE_NAME,
        WAITING_FOR_FILE_SIZE,
        FILE_RECEIVE_PROCESS
    }

    private IOFileUploaderService.Stage stage;

    private byte[] buffer;

    private Network network;
    private ServerFileExplorer serverFileExplorer;

    private Logger logger = LogManager.getLogger(IOFileUploaderService.class);

    public IOFileUploaderService(Network network, ServerFileExplorer serverFileExplorer) {
        this.network = network;
        this.serverFileExplorer = serverFileExplorer;
    }

    @Override
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

                    long numberOfParcels = fileSize / byteBufferSize;
                    int tailSize = (int) (fileSize - (numberOfParcels * byteBufferSize));

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
                    stage = Stage.WAITING_FOR_FILE_NAME_LENGTH;
                    return true;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }

        }
    }
}
