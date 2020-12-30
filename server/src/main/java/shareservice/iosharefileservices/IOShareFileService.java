package shareservice.iosharefileservices;

import entities.User;
import fileserivices.interfaces.ServerFileExplorer;
import network.interfaces.Network;
import shareservice.interfaces.ShareFile;
import shareservice.interfaces.ShareFileService;
import shareservice.interfaces.SharedFilesDirectoryContentSender;

public class IOShareFileService implements ShareFileService {

    private final SharedFilesDirectoryContentSender sharedFilesDirectoryContentSender;
    private final ShareFile shareFile;

    public IOShareFileService(User user, Network network, ServerFileExplorer serverFileExplorer) {
        this.sharedFilesDirectoryContentSender = new IOSharedFilesDirectoryContentSender(user, network);
        this.shareFile = new IOShareFile(user, network, serverFileExplorer);
    }

    @Override
    public void shareFileByCommandFromClient() {
        shareFile.shareFileByCommandFromClient();
    }

    @Override
    public void sendSharedFilesDirToClient() {
        sharedFilesDirectoryContentSender.sendSharedFilesDirectoryContent();
    }

}
