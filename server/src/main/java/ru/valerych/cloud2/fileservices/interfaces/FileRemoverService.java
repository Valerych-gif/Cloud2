package ru.valerych.cloud2.fileservices.interfaces;

import java.io.FileNotFoundException;

public interface FileRemoverService {
    void deleteFile() throws FileNotFoundException;
}
