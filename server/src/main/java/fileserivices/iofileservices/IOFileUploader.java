package fileserivices.iofileservices;

import fileserivices.interfaces.FileUploader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.*;


public class IOFileUploader implements FileUploader {

    private FileOutputStream fos;

    private final Logger logger = LogManager.getLogger(IOFileUploader.class);

    public IOFileUploader(File file) {
        try {
            this.fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            LogUtils.error(e.toString(), logger);
        }
    }

    public boolean writeBufferToFile(byte[] buffer){
        try {
            fos.write(buffer, 0, buffer.length);
        } catch (Exception e) {
            LogUtils.error(e.toString(), logger);
        }
        return false;
    }

    public void closeFile(){
        try {
            fos.close();
        } catch (IOException e) {
            LogUtils.error(e.toString(), logger);
        }
    }
}
