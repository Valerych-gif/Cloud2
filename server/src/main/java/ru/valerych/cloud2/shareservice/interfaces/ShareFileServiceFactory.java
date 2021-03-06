package ru.valerych.cloud2.shareservice.interfaces;

import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;

public interface ShareFileServiceFactory {
    ShareService createShareFileService(User user, Network network, ServerFileExplorer serverFileExplorer);
}
