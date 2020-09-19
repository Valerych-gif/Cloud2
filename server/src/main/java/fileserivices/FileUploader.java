package fileserivices;

import java.io.File;

public interface FileUploader {
    boolean writeBufferToFile(byte[] buffer);
}
