package shareservice.interfaces;

import entities.User;

import java.io.File;
import java.io.IOException;

public interface ShareFile {
    void shareFileByCommandFromClient();
    File[] getSharedFiles(int userId) throws IOException;
}
