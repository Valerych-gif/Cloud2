package ru.valerych.cloud2.shareservice.interfaces;

import java.io.FileNotFoundException;

public interface SharedFilesDirectoryContentSender {
    void sendSharedFilesDirectoryContent() throws FileNotFoundException;
}
