package fileserivices.interfaces;

import java.io.FileNotFoundException;

public interface FileDeleter {
    void deleteOneFile(String path) throws FileNotFoundException;
    void deleteDirectory(String path);
}
