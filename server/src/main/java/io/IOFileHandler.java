package io;

import files.CloudFile;
import main.FileHandler;
import main.ConnectionHandler;
import main.Responses;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class IOFileHandler extends FileHandler {
    private DataInputStream is;
    private DataOutputStream os;

    public IOFileHandler(ConnectionHandler connectionHandler) {
        super(connectionHandler);
        init();
    }

    @Override
    public void init() {
        this.is = ((IOConnectionHandler) connectionHandler).getDataInputStream();
        this.os = ((IOConnectionHandler) connectionHandler).getDataOutputStream();
    }

    public void sendDirContentToClient() {
        String requestedDirFromClient = connectionHandler.getStringFromClient();
        setCurrentStorageDir(requestedDirFromClient);

        if (currentStorageDir.getAbsolutePath().length() > rootStorageDir.getAbsolutePath().length()) {
            connectionHandler.sendResponse(DIR_PREFIX + PARENT_DIR_MARK);
        }
        for (File f : Objects.requireNonNull(currentStorageDir.listFiles())) {
            String fileName = f.getName();
            sendFileToClient(fileName);
        }
        connectionHandler.sendResponse(Responses.END_OF_DIR_CONTENT.getString());
    }

    private void sendFileToClient(String fileName) {
        File f = new File(fileName);
        if (f.isDirectory()) {
            connectionHandler.sendResponse(DIR_PREFIX + fileName);
        } else {
            connectionHandler.sendResponse(FILE_PREFIX + fileName);
        }
    }

    public void sendSharedFilesToClient() {
        try {
            File[] files;
            files = authService.getSharedFiles(connectionHandler.getUserId());
            for (File f : Objects.requireNonNull(files)) {
                String fileName = f.getAbsolutePath();
                sendFileToClient(fileName);
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

    public boolean loadFileToStorage(CloudFile clientFile) {
        File cloudFile = new File(currentStorageDir.getAbsolutePath() + "/" + clientFile.getName());
        if (!cloudFile.exists()) {
            try {
                if (!cloudFile.createNewFile()) return false;
            } catch (IOException e) {
                logger.error(e);
                e.printStackTrace();
            }
        }
        long fileLength = clientFile.getFileLength();
        try {
            System.out.println("Uploading...");

            FileOutputStream fos = new FileOutputStream(cloudFile);
            if (fileLength > 0) {
                long numberOfSends = fileLength / bufferSize;
                for (long i = 0; i <= numberOfSends; i++) {
                    int bytesRead = is.read(buffer);
                    System.out.print("\r" + i + "/" + numberOfSends);
                    fos.write(buffer, 0, bytesRead);
                }
                fos.flush();
            }
            fos.close();
            System.out.println("\nFile uploaded");
            connectionHandler.sendResponse(Responses.OK.getString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean getFileFromStorage(String fileName) {
        CloudFile file = new CloudFile(currentStorageDir.getAbsolutePath() + "/" + fileName);
        if (!file.exists()) file = new CloudFile(fileName);
        return getFile(file);
    }

    private boolean getFile(CloudFile file) {
        if (file.exists()) {
            connectionHandler.sendResponse(Responses.OK.getString());
            long fileLength = file.length();
            try {
                connectionHandler.sendResponse(String.valueOf(file.length()));
                if (fileLength > 0) {
                    FileInputStream fis = new FileInputStream(file);
                    long numberOfSends = fileLength / bufferSize;
                    for (long i = 0; i <= numberOfSends; i++) {
                        int byteRead = fis.read(buffer);
                        os.write(buffer, 0, byteRead);
                        os.flush();
                    }
                    fis.close();
                }
                Thread.sleep(50); // todo Костыль. Надо найти другое решение. Без этого в передачу файла попадает ответ сервера
                connectionHandler.sendResponse(Responses.OK.getString());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // todo Обработчик ошибки
            logger.error("Неправильное имя файла");
        }
        return false;
    }
}
