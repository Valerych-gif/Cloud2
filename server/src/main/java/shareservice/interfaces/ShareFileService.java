package shareservice.interfaces;

import entities.User;

public interface ShareFileService {
    void shareFileByCommandFromClient();

    void sendSharedFilesDirToClient();
}
