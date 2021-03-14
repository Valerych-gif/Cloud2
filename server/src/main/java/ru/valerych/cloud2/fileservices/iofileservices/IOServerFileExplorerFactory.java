package ru.valerych.cloud2.fileservices.iofileservices;

import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorerFactory;
import ru.valerych.cloud2.network.interfaces.Network;

public class IOServerFileExplorerFactory implements ServerFileExplorerFactory {

    @Override
    public IOServerFileExplorer createServerFileExplorer(User user, Network network) {
        return new IOServerFileExplorer(user);
    }
}