package ru.valerych.cloud2.entities;

import java.io.File;

public class CloudFile extends File {
    private long fileLength;

    public CloudFile(String pathname, long fileLength) {
        super(pathname);
        this.fileLength = fileLength;
    }

    public CloudFile(String pathname) {
        super(pathname);
        this.fileLength = 0;
    }

    public long getFileLength() {
        return fileLength;
    }
}
