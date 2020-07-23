package files;

import java.io.File;

public class CloudFile extends File {
    private long fileLength;

    public CloudFile(String pathname, long fileLength) {
        super(pathname);
        this.fileLength = fileLength;
    }

    public long getFileLength() {
        return fileLength;
    }
}
