package fileserivices.iofileservices;

import entities.User;
import fileserivices.interfaces.FileService;
import fileserivices.interfaces.FileServiceFactory;
import fileserivices.interfaces.ServerFileExplorer;
import network.interfaces.Network;

public class IOFileServiceFactory implements FileServiceFactory {
    @Override
    public FileService createFileService(Network network, ServerFileExplorer serverFileExplorer) {
        return new IOFileService(network, serverFileExplorer);
    }
}