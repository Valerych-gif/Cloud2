package ru.valerych.cloud2.fileservices.iofileservices;

import ru.valerych.cloud2.fileservices.interfaces.FileUploader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;


public class IOFileUploader implements FileUploader {

    private FileOutputStream fos;

    private final Logger logger = LogManager.getLogger(IOFileUploader.class);

    public IOFileUploader(File file) {
        try {
            this.fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            logger.error(e.toString());
        }
    }

    public boolean writeBufferToFile(byte[] buffer){
        try {
            fos.write(buffer, 0, buffer.length);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return false;
    }

    public void closeFile(){
        try {
            fos.close();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }
}
