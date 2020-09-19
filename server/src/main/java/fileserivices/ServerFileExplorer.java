package fileserivices;

import entities.FileInfo;

import java.io.File;
import java.util.List;

public abstract class ServerFileExplorer {



    abstract boolean goToDirectory(String dirPath);
    abstract List<FileInfo> getCurrentDirectoryContent();

    public abstract File getCurrentDirectory();
}
