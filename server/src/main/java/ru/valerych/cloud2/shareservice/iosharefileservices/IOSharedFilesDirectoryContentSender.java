package ru.valerych.cloud2.shareservice.iosharefileservices;

import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.shareservice.interfaces.SharedFilesDirectoryContentSender;

public class IOSharedFilesDirectoryContentSender implements SharedFilesDirectoryContentSender {

    private User user;
    private Network network;

    public IOSharedFilesDirectoryContentSender(User user, Network network) {
        this.user = user;
        this.network = network;
    }

    @Override
    public void sendSharedFilesDirectoryContent() {

    }

}
