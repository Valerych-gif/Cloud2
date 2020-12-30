package shareservice.interfaces;

import entities.User;
import fileserivices.interfaces.FileService;
import fileserivices.interfaces.ServerFileExplorer;
import network.interfaces.Network;

public interface ShareFileServiceFactory {
    ShareFileService createShareFileService(User user, Network network, ServerFileExplorer serverFileExplorer);
}
