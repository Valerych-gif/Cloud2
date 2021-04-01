package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.shareservice.interfaces.ShareFile;
import ru.valerych.cloud2.shareservice.interfaces.ShareFileService;
import ru.valerych.cloud2.shareservice.interfaces.SharedFilesDirectoryContentSender;

import java.io.FileNotFoundException;

public class IOShareFileService implements ShareFileService {

    private final SharedFilesDirectoryContentSender sharedFilesDirectoryContentSender;
    private final ShareFile shareFile;

    private final Logger logger = LogManager.getLogger(IOShareFileService.class.getName());

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
        try {
            sharedFilesDirectoryContentSender.sendSharedFilesDirectoryContent();
        } catch (FileNotFoundException e){
            logger.error(e);
        }
    }

}
