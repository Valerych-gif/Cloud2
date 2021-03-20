package ru.valerych.cloud2.fileservices.interfaces;

import java.io.IOException;

public interface FileDownloader {
    void readBufferFromFile(byte[] buffer) throws IOException;
}
