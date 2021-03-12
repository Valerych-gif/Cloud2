package ru.valerych.cloud2.shareservice.interfaces;

import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileserivices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;

public interface ShareFileServiceFactory {
    ShareFileService createShareFileService(User user, Network network, ServerFileExplorer serverFileExplorer);
}
