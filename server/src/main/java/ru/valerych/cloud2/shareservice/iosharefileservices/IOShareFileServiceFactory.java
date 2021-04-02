package ru.valerych.cloud2.shareservice.iosharefileservices;

import ru.valerych.cloud2.entities.User;

import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.shareservice.interfaces.ShareService;
import ru.valerych.cloud2.shareservice.interfaces.ShareFileServiceFactory;

public class IOShareFileServiceFactory implements ShareFileServiceFactory {
    @Override
    public ShareService createShareFileService(User user, Network network, ServerFileExplorer serverFileExplorer) {
        return new IOShareService(user, network, serverFileExplorer);
    }
}
