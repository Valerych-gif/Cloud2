package ru.valerych.cloud2.fileserivices.iofileservices;

import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.fileserivices.interfaces.ServerFileExplorer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;
import ru.valerych.cloud2.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class IOServerFileExplorer implements ServerFileExplorer {

    private File currentDirectory;
    private final File userRootDirectory;

    private final Logger logger = LogManager.getLogger(IOServerFileExplorer.class);

    public IOServerFileExplorer(User user) {
        userRootDirectory = user.getUserStorage();
        currentDirectory = userRootDirectory;
    }

    @Override
    public boolean goToDirectory(String dirPath) {
        LogUtils.info("Trying to change current directory to '" + dirPath + "'", logger);
        String directoryPath = dirPath.equals("") ? "" : Cloud2ServerSettings.FILE_SEPARATOR + dirPath;
        File directory = new File(currentDirectory.getPath() + directoryPath);
        boolean isDirectory = dirPath
                .equals(Cloud2ServerSettings.ROOT_DIR_MARK)||
                dirPath.equals(Cloud2ServerSettings.PARENT_DIR_MARK)||
                directory.isDirectory();
        if (!isDirectory) return false;
        switch (dirPath) {
            case Cloud2ServerSettings.PARENT_DIR_MARK:
                if (!currentDirectory.equals(userRootDirectory))
                    currentDirectory = new File(currentDirectory.getParent());
                break;
            case Cloud2ServerSettings.ROOT_DIR_MARK:
                currentDirectory = new File(userRootDirectory.getPath());
                break;
            default:
                currentDirectory = new File(currentDirectory.getPath() + directoryPath);
                break;
        }
        LogUtils.info("Current server directory is '" + currentDirectory.getAbsolutePath() + "'", logger);
        return currentDirectory.exists();
    }

    @Override
    public List<FileInfo> getCurrentDirectoryContent() {
        List<FileInfo> filesInfo = new ArrayList<>();
        if (!currentDirectory.equals(userRootDirectory)) {
            System.out.println(currentDirectory + " " + userRootDirectory);
            filesInfo.add(new FileInfo(Cloud2ServerSettings.ROOT_DIR_MARK, 0, FileInfo.Type.DIRECTORY));
            filesInfo.add(new FileInfo(Cloud2ServerSettings.PARENT_DIR_MARK, 0, FileInfo.Type.DIRECTORY));
        }
        File[] files = currentDirectory.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            filesInfo.add(getFileInfo(file.getName()));
        }
        return filesInfo;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    @Override
    public FileInfo getFileInfo(String fileName) {
        File file = new File(currentDirectory.getPath() + Cloud2ServerSettings.FILE_SEPARATOR + fileName);
        FileInfo.Type type = file.isDirectory() ? FileInfo.Type.DIRECTORY : FileInfo.Type.FILE;
        return new FileInfo(file.getName(), file.length(), type);
    }
}
