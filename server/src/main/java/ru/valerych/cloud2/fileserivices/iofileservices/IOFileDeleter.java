package ru.valerych.cloud2.fileserivices.iofileservices;

import ru.valerych.cloud2.fileserivices.interfaces.FileDeleter;

import java.io.File;
import java.io.FileNotFoundException;

public class IOFileDeleter implements FileDeleter {
    @Override
    public void deleteOneFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()){
            if (!file.delete()){
                throw new RuntimeException();
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    public void deleteDirectory(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files != null) {
            for (final File file : files) {
                deleteDirectory(file.getPath());
            }
        }
        if (!directory.delete()){
            throw new RuntimeException();
        }
    }
}