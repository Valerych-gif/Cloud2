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
import java.nio.file.Path;
import java.nio.file.Paths;

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
    }

    @Override
    public void connectionUpdate(CloudConnection connection) {
        this.connection = connection;
    }
}
