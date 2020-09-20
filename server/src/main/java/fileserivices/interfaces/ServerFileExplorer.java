package fileserivices.interfaces;

import entities.FileInfo;

import java.io.File;
import java.util.List;

public interface ServerFileExplorer {

    boolean goToDirectory(String dirPath);
    List<FileInfo> getCurrentDirectoryContent();

    File getCurrentDirectory();
}
