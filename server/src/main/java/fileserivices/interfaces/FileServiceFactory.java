package fileserivices.interfaces;

import entities.User;
import network.interfaces.Network;

public interface FileServiceFactory {
    FileService createFileService(Network network, ServerFileExplorer serverFileExplorer);
}
