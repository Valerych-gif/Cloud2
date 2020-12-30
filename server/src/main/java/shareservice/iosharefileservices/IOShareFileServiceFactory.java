package shareservice.iosharefileservices;

import entities.User;

import fileserivices.interfaces.ServerFileExplorer;
import network.interfaces.Network;
import shareservice.interfaces.ShareFileService;
import shareservice.interfaces.ShareFileServiceFactory;

public class IOShareFileServiceFactory implements ShareFileServiceFactory {
    @Override
    public ShareFileService createShareFileService(User user, Network network, ServerFileExplorer serverFileExplorer) {
        return new IOShareFileService(user, network, serverFileExplorer);
    }
}
