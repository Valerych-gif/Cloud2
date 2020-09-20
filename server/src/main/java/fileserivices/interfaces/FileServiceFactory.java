package fileserivices.interfaces;

import entities.User;
import network.interfaces.Network;

public interface FileServiceFactory {
    FileService createFileService(User user, Network network);
}
