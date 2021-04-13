package ru.valerych.cloud2.client.services.fileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.client.network.ConnectionObserver;
import ru.valerych.cloud2.client.network.Network;
import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.commands.Responses;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileDownloader implements ConnectionObserver {

    private static final int BUFFER_SIZE = 1024;
    private final Logger logger = LogManager.getLogger(FileDownloader.class.getName());

    private CloudConnection connection;

    public FileDownloader() {
        connection = new CloudConnection();
        connection.setAuthorized(false);
    }

    @Override
    public void connectionUpdate(CloudConnection connection) {
        this.connection = connection;
        logger.debug("FileDownloader. Connection status was changed. Authorized: " + connection.isAuthorized());
    }

    public void download(String requestedFileName, Path currentLocalDirectory) throws IOException, BadResponseException {
        Socket socket = connection.getSocket();
        if (!connection.isAuthorized()||socket==null||requestedFileName==null) return;
        logger.debug("Start of downloading of file " + requestedFileName);

        Network network = new Network(connection);

        network.sendByteToServer(Requests.DOWNLOAD.get());
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server isn't ready to send file.");

        int fileNameSize = requestedFileName.length();
        network.sendByteToServer((byte)fileNameSize);
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server didn't get filename size.");

        network.sendBytesToServer(requestedFileName.getBytes(StandardCharsets.UTF_8));
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server didn't get filename.");
        
        byte fileMark = network.readByteFromServer();
        boolean isDirectory = fileMark == 'D';
        logger.debug("File mark " + fileMark + " was received");
        
        long fileSize = network.readLongFromServer();
        logger.debug("File size " + fileSize + " was received");
        
        putFileIntoLocalStorage(network, currentLocalDirectory, requestedFileName, fileSize);
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server didn't send file.");
    }

    private void putFileIntoLocalStorage(Network network, Path currentLocalDirectory, String fileName, long fileSize) throws IOException {
        Path filePath = Paths.get(currentLocalDirectory.getFileName().toString(), fileName);
        if (!Files.exists(filePath))
            Files.createFile(filePath);
        else
            throw new FileAlreadyExistsException("File already exists"); //TODO If file exists stop download process and send stop command to server
        byte[] buffer;
        int parcelsQuantity = (int) fileSize/BUFFER_SIZE;
        logger.debug("File was splitted to " + parcelsQuantity + " parcels");
        int tailLength = (int) (fileSize - parcelsQuantity*BUFFER_SIZE);
        logger.debug("File has tail length " + tailLength);
        for (int i =0; i<parcelsQuantity; i++){
            buffer = network.readBytesFromServer(BUFFER_SIZE);
            Files.write(filePath, buffer, StandardOpenOption.APPEND);
        }
        buffer = network.readBytesFromServer(tailLength);
        Files.write(filePath, buffer, StandardOpenOption.APPEND);
    }
}
