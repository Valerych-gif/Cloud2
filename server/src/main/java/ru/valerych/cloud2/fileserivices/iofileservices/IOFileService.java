package ru.valerych.cloud2.fileserivices.iofileservices;

import ru.valerych.cloud2.fileserivices.interfaces.*;
import ru.valerych.cloud2.network.interfaces.Network;

import java.io.FileNotFoundException;

public class IOFileService implements FileService {

    private final FileUploaderService fileUploaderService;
    private final FileDownloaderService fileDownloaderService;
    private final FileDeleterService fileDeleterService;
    private final DirectoryContentSender directoryContentSender;

    public IOFileService(Network network, ServerFileExplorer serverFileExplorer) {
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
