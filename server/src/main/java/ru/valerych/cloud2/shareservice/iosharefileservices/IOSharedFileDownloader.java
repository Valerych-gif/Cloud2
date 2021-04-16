package ru.valerych.cloud2.shareservice.iosharefileservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.fileservices.interfaces.FileDownloader;
import ru.valerych.cloud2.fileservices.iofileservices.IOFileUploader;
import ru.valerych.cloud2.shareservice.interfaces.SharedFileDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class IOSharedFileDownloader implements SharedFileDownloader {

    private File file;
    private FileInputStream fis;

    private final Logger logger = LogManager.getLogger(IOFileUploader.class.getName());

    public IOSharedFileDownloader(File file) {
        try {
            this.file = file;
            this.fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void readBufferFromFile(byte[] buffer) throws IOException {
        int read = fis.read(buffer);
        if (read == -1) {
            throw new IOException(String.format("Can't read buffer from file. Nothing to read from file %s", file.getAbsolutePath()));
        }
    }

    public void closeFile() {
        try {
            fis.close();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public byte[] readBytesFromFile(int length) throws IOException {
        byte[] buffer = new byte[length];
        int read = 0;
        try {
            read = fis.read(buffer, 0, length);
        } catch (IOException e) {
            logger.error(e.toString());
        }
        if (read == -1) {
            throw new IOException(String.format("Can't read bytes from file. Nothing to read from file %s", file.getAbsolutePath()));
        }

        return buffer;
    }
}
