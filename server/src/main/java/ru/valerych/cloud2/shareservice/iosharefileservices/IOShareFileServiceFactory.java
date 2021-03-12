package ru.valerych.cloud2.shareservice.iosharefileservices;

import ru.valerych.cloud2.entities.User;

import ru.valerych.cloud2.fileserivices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.shareservice.interfaces.ShareFileService;
import ru.valerych.cloud2.shareservice.interfaces.ShareFileServiceFactory;

public class IOShareFileServiceFactory implements ShareFileServiceFactory {
    @Override
    public ShareFileService createShareFileService(User user, Network network, ServerFileExplorer serverFileExplorer) {
        return new IOShareFileService(user, network, serverFileExplorer);
    }
}
