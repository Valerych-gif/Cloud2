package ru.valerych.cloud2.client.services.fileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.client.network.ConnectionObserver;
import ru.valerych.cloud2.client.network.Network;
import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.commands.Responses;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static ru.valerych.cloud2.client.controllers.MainWindowController.DEFAULT_LOCAL_ROOT_DIRECTORY;

public class FileUploader implements ConnectionObserver {

    private static final int BUFFER_SIZE = 1024;
    private final Logger logger = LogManager.getLogger(FileUploader.class.getName());

    private CloudConnection connection;

    public void upload(String fileName, Path currentDirectory) throws IOException, BadResponseException {
        Socket socket = connection.getSocket();
        if (!connection.isAuthorized()||socket==null||fileName==null) return;
        Path path = Paths.get(currentDirectory.toString(), fileName);
        logger.debug("Start uploading file " + path);

        Network network = new Network(connection);

        network.sendByteToServer(Requests.UPLOAD.get());
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server isn't ready to receive file.");

        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        int fileNameSize = fileNameBytes.length;
        network.sendByteToServer((byte)fileNameSize);
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server didn't get filename size.");

        network.sendBytesToServer(fileNameBytes);
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server didn't get filename.");

        Path filePath = Paths.get(DEFAULT_LOCAL_ROOT_DIRECTORY, currentDirectory.toString(), fileName);
        if (!Files.exists(filePath))
            throw new FileNotFoundException("File isn't exists"); //TODO If file isn't exists stop upload process and send stop command to server
        long fileSize = Files.size(filePath);

        network.sendLongToServer(fileSize);
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server didn't get file size.");

        uploadProcess(network, filePath, fileSize);
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server didn't get file.");
    }

    private void uploadProcess(Network network, Path filePath, long fileSize) throws IOException {

        byte[] buffer = new byte[BUFFER_SIZE];
        int parcelsQuantity = (int) fileSize/BUFFER_SIZE;
        logger.debug("File was splitted to " + parcelsQuantity + " parcels");
        int tailLength = (int) (fileSize - parcelsQuantity*BUFFER_SIZE);
        logger.debug("File has tail length " + tailLength);

        DataInputStream inputStream = new DataInputStream(new FileInputStream(filePath.toString()));
        for (int i =0; i<parcelsQuantity; i++){
            inputStream.read(buffer);
            network.sendBytesToServer(buffer);
        }
        buffer = new byte[tailLength];
        inputStream.read(buffer);
        network.sendBytesToServer(buffer);
        inputStream.close();
    }

    @Override
    public void connectionUpdate(CloudConnection connection) {
        this.connection = connection;
    }
}
