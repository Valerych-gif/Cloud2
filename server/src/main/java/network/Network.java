package network;

import commands.Responses;
import entities.CloudFile;
import settings.Cloud2ServerSettings;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Network {
    public void sendDirContentToClient() throws IOException {
        String requestedDirFromClient = connectionHandler.getStringFromClient();
        setCurrentStorageDir(requestedDirFromClient);

        if (currentStorageDir.getAbsolutePath().length() > rootStorageDir.getAbsolutePath().length()) {
            connectionHandler.sendResponse(DIR_PREFIX + PARENT_DIR_MARK);
        }
        for (File f : Objects.requireNonNull(currentStorageDir.listFiles())) {
            String fileName = f.getName();
            sendFileNameToClient(fileName);
        }
        connectionHandler.sendResponse(Responses.END_OF_DIR_CONTENT.getString());
    }

    private void sendFileNameToClient(String fileName) {
        File f = new File(fileName);
        if (f.isDirectory()) {
            connectionHandler.sendResponse(DIR_PREFIX + fileName);
        } else {
            connectionHandler.sendResponse(FILE_PREFIX + fileName);
        }
    }

    public void sendSharedFileNamesToClient() {
        try {
            File[] files;
            files = authService.getSharedFiles(connectionHandler.getUserId());
            for (File f : Objects.requireNonNull(files)) {
                String fileName = f.getAbsolutePath();
                File storageAbsPath = new File(Cloud2ServerSettings.STORAGE_ROOT_DIR);
                int storageAbsPathLength = storageAbsPath.getAbsolutePath().length();
                String fileNameToSend = fileName.substring(storageAbsPathLength);
                System.out.println(fileNameToSend);
                sendFileNameToClient(fileNameToSend);
            }
            connectionHandler.sendResponse(Responses.END_OF_DIR_CONTENT.getString());
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }

    }

    public void setCurrentStorageDir(String fileName) {
        String newFileName;
        CloudFile file;
        if (fileName.equals(PARENT_DIR_MARK)) {
            file = new CloudFile(currentStorageDir.getParent());
            if (file.getAbsolutePath().length() <= rootStorageDir.getAbsolutePath().length()) {
                file = new CloudFile(rootStorageDir.getAbsolutePath());
            }
        } else {
            newFileName = currentStorageDir.getAbsolutePath() + "/" + fileName;
            file = new CloudFile(newFileName);
        }

        if (file.exists() && file.isDirectory()) {
            currentStorageDir = file;
        }
    }
}
