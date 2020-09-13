package fileserivices;

import entities.FileInfo;

import java.util.List;

public abstract class ServerFileExplorer {



    abstract boolean goToDirectory(String dirPath);
    abstract List<FileInfo> getCurrentDirectoryContent();
}
