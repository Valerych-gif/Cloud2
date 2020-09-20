package fileserivices.iofileservices;

import entities.User;
import fileserivices.interfaces.DirectoryContentSender;
import fileserivices.interfaces.FileDownloaderService;
import fileserivices.interfaces.FileService;
import fileserivices.interfaces.FileUploaderService;
import network.interfaces.Network;

public class IOFileService implements FileService {

    private FileUploaderService fileUploaderService;
    private FileDownloaderService fileDownloaderService;
    private DirectoryContentSender directoryContentSender;

    public IOFileService(User user, Network network) {
        IOServerFileExplorer serverFileExplorer = new IOServerFileExplorer(user);
        this.fileUploaderService = new IOFileUploaderService(network, serverFileExplorer);
        this.fileDownloaderService = new IOFileDownloaderService(network, serverFileExplorer);
        this.directoryContentSender = new IODirectoryContentSender(network, serverFileExplorer);
    }

    public boolean sendDirContent() {
        return directoryContentSender.sendDirectoryContent();
    }

    public boolean receiveFileFromClient() {
        return fileUploaderService.receiveFileFromClient();
    }

    public boolean sendFileToClient(){
        return fileDownloaderService.sendFileToClient();
    }
}
