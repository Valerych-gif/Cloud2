package fileserivices.iofileservices;

import entities.User;
import fileserivices.interfaces.DirectoryContentSender;
import fileserivices.interfaces.FileDownloaderService;
import fileserivices.interfaces.FileService;
import fileserivices.interfaces.FileUploaderService;
import network.interfaces.Network;

import java.io.FileNotFoundException;

public class IOFileService implements FileService {

    private final FileUploaderService fileUploaderService;
    private final FileDownloaderService fileDownloaderService;
    private final IOFileDeleterService fileDeleterService;
    private final DirectoryContentSender directoryContentSender;

    public IOFileService(User user, Network network) {
        IOServerFileExplorer serverFileExplorer = new IOServerFileExplorer(user);
        this.fileUploaderService = new IOFileUploaderService(network, serverFileExplorer);
        this.fileDownloaderService = new IOFileDownloaderService(network, serverFileExplorer);
        this.fileDeleterService = new IOFileDeleterService(network, serverFileExplorer);
        this.directoryContentSender = new IODirectoryContentSender(network, serverFileExplorer);
    }

    @Override
    public void sendDirContent() {
        directoryContentSender.sendDirectoryContent();
    }

    @Override
    public void receiveFileFromClient() {
        fileUploaderService.receiveFileFromClient();
    }

    @Override
    public void sendFileToClient(){
        fileDownloaderService.sendFileToClient();
    }

    @Override
    public void deleteFileFromServer() throws FileNotFoundException {
        fileDeleterService.deleteFile();
    }
}
