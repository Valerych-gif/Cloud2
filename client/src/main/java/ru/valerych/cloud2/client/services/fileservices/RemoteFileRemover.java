package ru.valerych.cloud2.client.services.fileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.client.network.ConnectionObserver;
import ru.valerych.cloud2.client.network.Network;
import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.commands.Responses;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RemoteFileRemover implements ConnectionObserver {

    private final Logger logger = LogManager.getLogger(RemoteFileRemover.class.getName());

    private CloudConnection connection;
    @Override
    public void connectionUpdate(CloudConnection connection) {
        this.connection = connection;
    }

    public void delete(String fileName) throws BadResponseException {
        logger.debug("File " + fileName + " will be removed");

        Socket socket = connection.getSocket();
        if (!connection.isAuthorized()||socket==null||fileName==null) return;
        logger.debug("Start of deleting file " + fileName);

        Network network = new Network(connection);

        network.sendByteToServer(Requests.DELETE.get());
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server isn't ready to delete file.");

        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        int fileNameSize = fileNameBytes.length;
        network.sendByteToServer((byte)fileNameSize);
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server didn't get file name size.");

        network.sendBytesToServer(fileNameBytes);
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server didn't get file name.");
    }
}
