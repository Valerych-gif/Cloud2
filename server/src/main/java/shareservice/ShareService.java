package shareservice;

import entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import settings.Cloud2ServerSettings;
import utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class ShareService {

    public final static Path SHARE_FILE_PATH = Paths.get(
            Cloud2ServerSettings.SERVER_MAIN_FILES_DIR,
            Cloud2ServerSettings.AUTH_FILE
    );
    private Logger logger = LogManager.getLogger(ShareService.class);
    
    public void shareFile(User sender, User receiver, String fileName) throws IOException {
        String shareLine = sender.getId() + " " + receiver.getId() + " " + fileName + "\r\n";
        Files.write(SHARE_FILE_PATH, shareLine.getBytes(), StandardOpenOption.APPEND);
        LogUtils.info(shareLine, logger, "В файл " + SHARE_FILE_PATH + " записана строка ");
    }

    public File[] getSharedFiles(int userId) throws IOException {
        String id = String.valueOf(userId);
        List<String[]> fileNames = Files.lines(SHARE_FILE_PATH)
                .map((str) -> str.split(" "))
                .filter(str->str[0].equals(id)||str[0].equals("-1"))
                .collect(Collectors.toList());
        File[] files = new File[fileNames.size()];
        for (int i=0; i< files.length; i++) {
            files[i] = new File(Cloud2ServerSettings.STORAGE_ROOT_DIR + "/" + fileNames.get(i)[1] + "/" + fileNames.get(i)[2]);
        }
        return files;
    }

    public void shareFileByCommandFromClient() {
    }
}
