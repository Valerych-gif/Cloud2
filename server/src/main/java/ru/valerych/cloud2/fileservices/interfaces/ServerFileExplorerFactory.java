package ru.valerych.cloud2.fileservices.interfaces;

import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.network.interfaces.Network;

public interface ServerFileExplorerFactory {
    ServerFileExplorer createServerFileExplorer(User user, Network network);
}
