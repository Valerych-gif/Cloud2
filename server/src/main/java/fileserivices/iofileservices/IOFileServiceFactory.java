package fileserivices.iofileservices;

import entities.User;
import fileserivices.interfaces.FileService;
import fileserivices.interfaces.FileServiceFactory;
import network.interfaces.Network;

public class IOFileServiceFactory implements FileServiceFactory {
    @Override
    public FileService createFileService(User user, Network network) {
        return new IOFileService(user, network);
    }
}