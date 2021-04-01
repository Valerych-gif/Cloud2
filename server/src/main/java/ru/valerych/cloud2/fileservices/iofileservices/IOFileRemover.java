package ru.valerych.cloud2.fileservices.iofileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.fileservices.interfaces.FileRemover;
import ru.valerych.cloud2.network.ionetwork.IONetwork;

import java.io.File;
import java.io.FileNotFoundException;

public class IOFileRemover implements FileRemover {

    private final Logger logger = LogManager.getLogger(IOFileRemover.class.getName());

    @Override
    public void deleteOneFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()){
            if (!file.delete()){
                logger.error(String.format("File '%s' wasn't removed", path));
                throw new RuntimeException(String.format("File '%s' wasn't removed", path));
            }
        } else {
            logger.error(String.format("File '%s' isn't exist", path));
            throw new FileNotFoundException(String.format("File '%s' isn't exist", path));
        }
    }

    @Override
    public void deleteDirectory(String path) throws FileNotFoundException {
        File directory = new File(path);
        if (!directory.exists()){
            logger.error(String.format("Directory '%s' isn't exist", path));
            throw new FileNotFoundException(String.format("Directory '%s' isn't exist", path));
        }
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
