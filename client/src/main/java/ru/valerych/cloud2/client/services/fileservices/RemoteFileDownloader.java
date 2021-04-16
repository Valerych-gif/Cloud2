package ru.valerych.cloud2.client.services.fileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.network.Network;
import ru.valerych.cloud2.commands.Requests;
import ru.valerych.cloud2.commands.Responses;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;

public class RemoteFileDownloader extends FileDownloader{

    private final Logger logger = LogManager.getLogger(RemoteFileDownloader.class.getName());

    @Override
    public void download(String requestedFileName, Path currentLocalDirectory) throws IOException, BadResponseException {
        Socket socket = connection.getSocket();
        if (!connection.isAuthorized()||socket==null||requestedFileName==null) return;
        logger.debug("Start of downloading of file " + requestedFileName);

        network = new Network(connection);

        network.sendByteToServer(Requests.DOWNLOAD.get());
        if (network.readByteFromServer()== Responses.FAIL.getSignalByte()) throw new BadResponseException("Server isn't ready to send file.");

        super.download(requestedFileName, currentLocalDirectory);
    }
}
