package ru.valerych.cloud2.shareservice.interfaces;

import java.io.File;
import java.io.IOException;

public interface ShareFile {
    void shareFileByCommandFromClient();
    File[] getSharedFiles(int userId) throws IOException;
}
