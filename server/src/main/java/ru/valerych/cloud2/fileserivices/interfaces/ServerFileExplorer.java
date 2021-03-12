package ru.valerych.cloud2.fileserivices.interfaces;

import ru.valerych.cloud2.entities.FileInfo;

import java.io.File;
import java.util.List;

public interface ServerFileExplorer {

    boolean goToDirectory(String dirPath);
    List<FileInfo> getCurrentDirectoryContent();

    File getCurrentDirectory();

    FileInfo getFileInfo(String fileName);
}
