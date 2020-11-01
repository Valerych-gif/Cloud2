package fileserivices.interfaces;

import java.io.FileNotFoundException;

public interface FileDeleterService {
    void deleteFile() throws FileNotFoundException;
}
