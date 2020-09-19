package fileserivices;

import entities.FileInfo;
import entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IOServerFileExplorer extends ServerFileExplorer {

    private File currentDirectory;
    private File userRootDirectory;

    private Logger logger = LogManager.getLogger(IOServerFileExplorer.class);

    public IOServerFileExplorer(User user) {
        userRootDirectory = user.getUserStorage();
        currentDirectory = userRootDirectory;
    }

    @Override
    public boolean goToDirectory(String dirPath) {
        LogUtils.info("Trying to change current directory to '" + dirPath + "'", logger);
        String directoryPath = dirPath.equals("") ? "" : "/" + dirPath;
        File directory = new File(currentDirectory.getPath() + directoryPath);
        boolean isDirectory = dirPath.equals(ROOT_DIR_MARK)||dirPath.equals(PARENT_DIR_MARK)|| directory.isDirectory();
        if (!isDirectory) return false;
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
        LogUtils.info("Current server directory is '" + currentDirectory.getAbsolutePath() + "'", logger);
        return currentDirectory.exists();
    }

    @Override
    public List<FileInfo> getCurrentDirectoryContent() {
        List<FileInfo> filesInfo = new ArrayList<>();
        if (!currentDirectory.equals(userRootDirectory)) {
            System.out.println(currentDirectory + " " + userRootDirectory);
            filesInfo.add(new FileInfo(ROOT_DIR_MARK, 0, FileInfo.Type.DIRECTORY));
            filesInfo.add(new FileInfo(PARENT_DIR_MARK, 0, FileInfo.Type.DIRECTORY));
        }
        File[] files = currentDirectory.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            FileInfo.Type type = file.isDirectory() ? FileInfo.Type.DIRECTORY : FileInfo.Type.FILE;
            filesInfo.add(new FileInfo(file.getName(), file.length(), type));
        }
        return filesInfo;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }
}
