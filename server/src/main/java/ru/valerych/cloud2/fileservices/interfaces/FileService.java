package ru.valerych.cloud2.fileservices.interfaces;

import java.io.FileNotFoundException;

public interface FileService {
    void receiveFileFromClient();
    void sendDirContent();
    void sendFileToClient();
    void deleteFileFromServer() throws FileNotFoundException;
}
