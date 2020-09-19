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

import java.nio.ByteBuffer;
import java.util.List;

public class FileService {


    private FileUploaderService fileUploaderService;
    private DirectoryContentSender directoryContentSender;

    private Network network;
    private User user;

    public FileService(User user, Network network) {
        if (network instanceof IONetwork) {
            IOServerFileExplorer serverFileExplorer = new IOServerFileExplorer(user);
            this.fileUploaderService = new IOFileUploaderService(network, serverFileExplorer);
            this.directoryContentSender = new IODirectoryContentSender(network, serverFileExplorer);
            this.network = network;
            this.user = user;
        }
    }

    public boolean sendDirContent() {
        return directoryContentSender.sendDirectoryContent();
    }

    public boolean receiveFileFromClient() {
        return fileUploaderService.receiveFileFromClient();
    }
}
