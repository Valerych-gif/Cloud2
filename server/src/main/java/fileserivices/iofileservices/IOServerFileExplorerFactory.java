package fileserivices.iofileservices;

import entities.User;
import fileserivices.interfaces.FileService;
import fileserivices.interfaces.FileServiceFactory;
import fileserivices.interfaces.ServerFileExplorerFactory;
import network.interfaces.Network;

public class IOServerFileExplorerFactory implements ServerFileExplorerFactory {

    @Override
    public IOServerFileExplorer createServerFileExplorer(User user, Network network) {
        return new IOServerFileExplorer(user);
    }
}