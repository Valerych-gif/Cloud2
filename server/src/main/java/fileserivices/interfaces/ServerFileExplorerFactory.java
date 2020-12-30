package fileserivices.interfaces;

import entities.User;
import network.interfaces.Network;

public interface ServerFileExplorerFactory {
    ServerFileExplorer createServerFileExplorer(User user, Network network);
}
