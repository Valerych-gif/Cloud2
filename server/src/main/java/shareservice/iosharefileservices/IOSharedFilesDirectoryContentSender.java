package shareservice.iosharefileservices;

import entities.User;
import network.interfaces.Network;
import shareservice.interfaces.SharedFilesDirectoryContentSender;

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
