package ru.valerych.cloud2.shareservice.interfaces;

import java.io.IOException;

public interface SharedFileDownloader {
    void readBufferFromFile(byte[] buffer) throws IOException;
    void closeFile();
    byte[] readBytesFromFile(int tailSize) throws IOException;
}
