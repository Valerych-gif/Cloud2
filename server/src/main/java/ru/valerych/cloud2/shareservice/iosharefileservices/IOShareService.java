package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import ru.valerych.cloud2.network.interfaces.Network;
import ru.valerych.cloud2.shareservice.interfaces.ShareFileService;
import ru.valerych.cloud2.shareservice.interfaces.ShareService;
import ru.valerych.cloud2.shareservice.interfaces.SharedFilesDirectoryContentSender;

import java.io.FileNotFoundException;

public class IOShareService implements ShareService {

    private final SharedFilesDirectoryContentSender sharedFilesDirectoryContentSender;
    private final ShareFileService shareFileService;

    private final Logger logger = LogManager.getLogger(IOShareService.class.getName());

    public IOShareService(User user, Network network, ServerFileExplorer serverFileExplorer) {
        this.sharedFilesDirectoryContentSender = new IOSharedFilesDirectoryContentSender(user, network);
        this.shareFileService = new IOShareFileService(user, network, serverFileExplorer);
    }

    @Override
    public void shareFileByCommandFromClient() {
        shareFileService.shareFileByCommandFromClient();
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
