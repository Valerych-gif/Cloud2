package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.authservice.IOAuthorisationService;
import ru.valerych.cloud2.authservice.interfaces.AuthorisationService;
import ru.valerych.cloud2.commands.Responses;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.UserNotFoundException;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.shareservice.interfaces.ShareFileService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class IOShareFileService implements ShareFileService {

    private enum Stage {
        WAITING_FOR_USERNAME_LENGTH,
        WAITING_FOR_USERNAME,
        WAITING_FOR_FILENAME_LENGTH,
        WAITING_FOR_FILENAME,
        SHARE_FILE_PROCESS
    }

    private final User user;
    private final Network network;
    private final ServerFileExplorer serverFileExplorer;

    public IOShareFileService(User user, Network network, ServerFileExplorer serverFileExplorer) {
        this.user = user;
        this.network = network;
        this.serverFileExplorer = serverFileExplorer;
    }

    public final static File SHARE_FILE = Paths.get(
            Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
            Cloud2ServerSettings.SHARED_FILE
    ).toFile();
    private final Logger logger = LogManager.getLogger(IOShareFileService.class.getName());

    @Override
    public void shareFileByCommandFromClient() {
        int userNameLength = 0, fileNameLength = 0;
        String userName = "", fileName = "";
        Stage stage = Stage.WAITING_FOR_USERNAME_LENGTH;
        boolean isFileShared = false;
        while (!isFileShared) {
            switch (stage) {
                case WAITING_FOR_USERNAME_LENGTH:
                    userNameLength = network.readByteFromClient();
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    logger.info("Length of username '" + userNameLength + "' was received");
                    stage = Stage.WAITING_FOR_USERNAME;
                    break;
                case WAITING_FOR_USERNAME:
                    userName = new String(network.readBytesFromClient(userNameLength));
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    logger.info("Username '" + userName + "' was received");
                    stage = Stage.WAITING_FOR_FILENAME_LENGTH;
                    break;
                case WAITING_FOR_FILENAME_LENGTH:
                    fileNameLength = network.readByteFromClient();
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    logger.info("Length of file name '" + fileNameLength + "' was received");
                    stage = Stage.WAITING_FOR_FILENAME;
                    break;
                case WAITING_FOR_FILENAME:
                    fileName = new String(network.readBytesFromClient(fileNameLength));
                    network.sendByteToClient(Responses.OK.getSignalByte());
                    logger.info("File name '" + fileName + "' was received");
                    stage = Stage.SHARE_FILE_PROCESS;
                    break;
                case SHARE_FILE_PROCESS:
                    AuthorisationService authorisationService = new IOAuthorisationService();
                    try {
                        int receiverId = authorisationService.getUserIdByLogin(userName);
                        shareFile(receiverId, fileName);
                    } catch (UserNotFoundException e) {
                        logger.error(String.format("Attempt to share file '%s' to user '%s' failed. User not found", fileName, userName));
                    }
                    isFileShared = true;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }
        }
    }

    private void shareFile(int receiverId, String fileName) {
        String pathToFile = serverFileExplorer.getCurrentDirectory().getPath();
        String shareLine = user.getId() + " " + receiverId + " " + pathToFile + Cloud2ServerSettings.FILE_SEPARATOR + fileName + System.lineSeparator();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SHARE_FILE, true))) {
            writer.write(shareLine);
            writer.flush();
            logger.info(String.format("There was written string '%s' in file '%s'", shareLine, SHARE_FILE.getAbsolutePath()));
        } catch (IOException e) {
            logger.error(String.format("There was unsuccessful attempt to write string '%s' in file '%s'", shareLine, SHARE_FILE.getAbsolutePath()));
        }
    }
}
