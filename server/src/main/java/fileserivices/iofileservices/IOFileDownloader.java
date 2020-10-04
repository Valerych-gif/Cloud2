package fileserivices.iofileservices;

import fileserivices.interfaces.FileDownloader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class IOFileDownloader implements FileDownloader {

    private FileInputStream fis;

    private Logger logger = LogManager.getLogger(IOFileUploader.class);

    public IOFileDownloader(File file) {
        try {
            this.fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LogUtils.error(e.toString(), logger);
        }
    }

    @Override
    public boolean readBufferFromFile(byte[] buffer) {
        try {
            fis.read(buffer);
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
        return false;
    }

    public boolean isFileEnded() {
        try {
            return fis.available() <= 0;
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
        return false;
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
            fis.read(buffer, 0, length);
            return buffer;
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
        return buffer;
    }
}
