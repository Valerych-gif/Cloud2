package ru.valerych.cloud2.fileservices.iofileservices;

import ru.valerych.cloud2.fileservices.interfaces.*;
import ru.valerych.cloud2.network.interfaces.Network;

import java.io.FileNotFoundException;

public class IOFileService implements FileService {

    private final FileUploaderService fileUploaderService;
    private final FileDownloaderService fileDownloaderService;
    private final FileRemoverService fileRemoverService;
    private final DirectoryContentSender directoryContentSender;

    public IOFileService(Network network, ServerFileExplorer serverFileExplorer) {
        this.fileUploaderService = new IOFileUploaderService(network, serverFileExplorer);
        this.fileDownloaderService = new IOFileDownloaderService(network, serverFileExplorer);
        this.fileRemoverService = new IOFileRemoverService(network, serverFileExplorer);
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
        fileRemoverService.deleteFile();
    }
}
