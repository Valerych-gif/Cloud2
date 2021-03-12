package ru.valerych.cloud2.fileserivices.iofileservices;

import ru.valerych.cloud2.fileserivices.interfaces.FileDownloader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class IOFileDownloader implements FileDownloader {

    private FileInputStream fis;

    private final Logger logger = LogManager.getLogger(IOFileUploader.class);

    public IOFileDownloader(File file) {
        try {
            this.fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LogUtils.error(e.toString(), logger);
        }
    }

    @Override
    public void readBufferFromFile(byte[] buffer) {
        try {
            int read = fis.read(buffer);
            if (read==-1){
                throw new RuntimeException();
            }
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
    }

    public void closeFile() {
        try {
            fis.close();
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
    }

    public byte[] readBytesFromFile(int length) {
        byte[] buffer = new byte[length];
        try {
            if (!(fis.read(buffer, 0, length)==-1)){
                return buffer;
            } else {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
        return buffer;
    }
}
