package fileserivices;

import java.io.File;

public interface FileUploaderService {
    boolean writeBufferToFile(byte[] buffer, File file);
}
