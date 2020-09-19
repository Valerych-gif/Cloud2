package fileserivices;

import entities.FileInfo;

import java.io.File;
import java.util.List;

public abstract class ServerFileExplorer {

    protected final static String PARENT_DIR_MARK = "..";
    protected final static String ROOT_DIR_MARK = ".";

    abstract boolean goToDirectory(String dirPath);
    abstract List<FileInfo> getCurrentDirectoryContent();

    public abstract File getCurrentDirectory();
}
