package ru.valerych.cloud2.fileservices.interfaces;

import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.exceptions.IsNotDirectoryException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface ServerFileExplorer {

    File goToDirectory(String dirPath) throws IsNotDirectoryException, FileNotFoundException;
    List<FileInfo> getCurrentDirectoryContent();

    File getCurrentDirectory();

    FileInfo getFileInfo(String fileName);
}
