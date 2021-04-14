package ru.valerych.cloud2.fileservices.iofileservices;

import ru.valerych.cloud2.entities.FileInfo;
import ru.valerych.cloud2.entities.User;
import ru.valerych.cloud2.exceptions.IsNotDirectoryException;
import ru.valerych.cloud2.fileservices.interfaces.ServerFileExplorer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.settings.Cloud2ServerSettings;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.valerych.cloud2.settings.Cloud2ServerSettings.*;


public class IOServerFileExplorer implements ServerFileExplorer {

    private File currentDirectory;
    private final File userRootDirectory;

    private final Logger logger = LogManager.getLogger(IOServerFileExplorer.class.getName());

    public IOServerFileExplorer(User user) {
        userRootDirectory = user.getUserStorage();
        currentDirectory = userRootDirectory;
    }

    @Override
    public File goToDirectory(String dirPath) throws IsNotDirectoryException, FileNotFoundException {
        String directoryPath = dirPath.equals("") ? "" : FILE_SEPARATOR + dirPath;
        logger.info("Trying to change current directory to '" + directoryPath + "'");
        File directory = new File(currentDirectory.getPath() + directoryPath);
        if (!directory.exists()) throw new FileNotFoundException(String.format("Directory '%s' isn't exists. Attempt to change directory failed", directory.getAbsolutePath()));
        boolean isDirectory = dirPath
                .equals(ROOT_DIR_MARK)||
                dirPath.equals(PARENT_DIR_MARK)||
                directory.isDirectory();
        if (!isDirectory) throw new IsNotDirectoryException(String.format("'%s' isn't directory. Attempt to change directory failed", directory.getAbsolutePath()));
        switch (dirPath) {
            case PARENT_DIR_MARK:
                if (!currentDirectory.equals(userRootDirectory))
                    currentDirectory = new File(currentDirectory.getParent());
                break;
            case ROOT_DIR_MARK:
                currentDirectory = new File(userRootDirectory.getPath());
                break;
            default:
                currentDirectory = new File(currentDirectory.getPath() + directoryPath);
                break;
        }
        logger.info("Current server directory is '" + currentDirectory.getAbsolutePath() + "'");
        return currentDirectory;
    }

    @Override
    public List<FileInfo> getCurrentDirectoryContent() {
        List<FileInfo> filesInfo = new ArrayList<>();
        if (!currentDirectory.equals(userRootDirectory)) {
            filesInfo.add(new FileInfo(ROOT_DIR_MARK, 0, FileInfo.Type.DIRECTORY));
            filesInfo.add(new FileInfo(PARENT_DIR_MARK, 0, FileInfo.Type.DIRECTORY));
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
        File file = new File(currentDirectory.getPath() + FILE_SEPARATOR + fileName);
        FileInfo.Type type = file.isDirectory() ? FileInfo.Type.DIRECTORY : FileInfo.Type.FILE;
        return new FileInfo(file.getName(), file.length(), type);
    }
}
