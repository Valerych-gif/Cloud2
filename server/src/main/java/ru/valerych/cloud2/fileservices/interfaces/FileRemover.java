package ru.valerych.cloud2.fileservices.interfaces;

import java.io.FileNotFoundException;

public interface FileRemover {
    void deleteOneFile(String path) throws FileNotFoundException;
    void deleteDirectory(String path) throws FileNotFoundException;
}
