package shareservice.iosharefileservices;

import authservice.UsersService;
import commands.Responses;
import entities.FileInfo;
import entities.User;
import fileserivices.interfaces.ServerFileExplorer;
import fileserivices.iofileservices.IODirectoryContentSender;
import network.interfaces.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import shareservice.interfaces.ShareFile;
import sun.nio.ch.Net;
import utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IOShareFile implements ShareFile {

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

    public IOShareFile(User user, Network network, ServerFileExplorer serverFileExplorer) {
        this.user = user;
        this.network = network;
        this.serverFileExplorer = serverFileExplorer;
    }

    public final static Path SHARE_FILE_PATH = Paths.get(
            Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
            Cloud2ServerSettings.SHARED_FILE
    );
    private final Logger logger = LogManager.getLogger(IOShareFile.class);

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
                    LogUtils.info("Length of username '" + userNameLength + "' was received", logger);
                    stage = Stage.WAITING_FOR_USERNAME;
                    break;
                case WAITING_FOR_USERNAME:
                    userName = new String(network.readBytesFromClient(userNameLength));
                    LogUtils.info("Username '" + userName + "' was received", logger);
                    stage = Stage.WAITING_FOR_FILENAME_LENGTH;
                    break;
                case WAITING_FOR_FILENAME_LENGTH:
                    fileNameLength = network.readByteFromClient();
                    LogUtils.info("Length of file name '" + fileNameLength + "' was received", logger);
                    stage = Stage.WAITING_FOR_FILENAME;
                    break;
                case WAITING_FOR_FILENAME:
                    fileName = new String(network.readBytesFromClient(fileNameLength));
                    LogUtils.info("File name '" + fileName + "' was received", logger);
                    stage = Stage.SHARE_FILE_PROCESS;
                    break;
                case SHARE_FILE_PROCESS:
                    int receiverId = getUserIdByUsername(userName);
                    shareFile(receiverId, fileName);
                    isFileShared = true;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + stage);
            }
        }
    }

    private void shareFile(int receiverId, String fileName) {
        String pathToFile = serverFileExplorer.getCurrentDirectory().getPath();
        String shareLine = user.getId() + " " + receiverId + " " + pathToFile + Cloud2ServerSettings.FILE_SEPARATOR + fileName + "\r\n";
        try {
            Files.write(SHARE_FILE_PATH, shareLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
        LogUtils.info(shareLine, logger, "В файл " + SHARE_FILE_PATH + " записана строка ");
    }

    public int getUserIdByUsername(String username) {
        try {
            Optional<String[]> userStringArray = Files.lines(UsersService.AUTH_FILE_PATH)
                    .map(s -> s.split(" "))
                    .filter(s -> s[1].equals(username))
                    .findFirst();
            return userStringArray.map(strings -> Integer.parseInt(strings[0])).orElse(-1);
        } catch (IOException e) {
            LogUtils.error("Authorization file can not be read", logger);
        }
        return -1;
    }

    public File[] getSharedFiles(int userId) throws IOException {
        String id = String.valueOf(userId);
        List<String[]> fileNames = Files.lines(SHARE_FILE_PATH)
                .map((str) -> str.split(" "))
                .filter(str -> str[0].equals(id) || str[0].equals("-1"))
                .collect(Collectors.toList());
        File[] files = new File[fileNames.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(
                    Cloud2ServerSettings.STORAGE_ROOT_DIR
                            + Cloud2ServerSettings.FILE_SEPARATOR
                            + fileNames.get(i)[1]
                            + Cloud2ServerSettings.FILE_SEPARATOR
                            + fileNames.get(i)[2]);
        }
        return files;
    }
}