package main;

import java.io.File;

public class CloudFile extends File{

    private boolean isDirectory;
    private long length;

    public CloudFile(String fileName) {
        super(fileName);
        this.isDirectory = false;
        this.length=super.length();
    }

    public CloudFile(String fileName, boolean isDirectory) {
        super(fileName);
        this.isDirectory = isDirectory;
        this.length=super.length();
    }

    public CloudFile(String fileName, boolean isDirectory, long length) {
        super(fileName);
        this.isDirectory = isDirectory;
        this.length=length;
    }


    public boolean isDirectory() {
        return isDirectory;
    }

    public long getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "CloudFile{" +
                "isDirectory=" + isDirectory +
                ", length=" + length +
                '}';
    }
}
