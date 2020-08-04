package io;

import files.CloudFile;
import main.Cloud2ServerStarter;
import main.FileHandler;
import main.ConnectionHandler;
import main.Responses;

import java.io.*;
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

        File file = new File(currentStorageDir.getAbsolutePath());
        if (!file.getAbsolutePath().equals(rootStorageDir.getAbsolutePath())) {
            connectionHandler.sendResponse(DIR_MARK + PARENT_DIR_MARK);
        }
        for (File f : Objects.requireNonNull(file.listFiles())) {
            String fileName = f.getName();
            if (f.isDirectory()) {
                connectionHandler.sendResponse(DIR_MARK + fileName);
            } else {
                connectionHandler.sendResponse(FILE_MARK + fileName);
            }
        }

        connectionHandler.sendResponse(Responses.END_OF_DIR_CONTENT.getString());
    }

    public void setCurrentStorageDir(String fileName) {
        String newFileName;
        CloudFile file;
        if (fileName.equals(PARENT_DIR_MARK)) {
            file = new CloudFile(currentStorageDir.getParent());
        } else {
            newFileName = currentStorageDir.getAbsolutePath() + "/" + fileName;
            file = new CloudFile(newFileName);
        }

        if (file.exists() && file.isDirectory()) {
            currentStorageDir = file;
        }
    }

    public boolean loadFileToStorage(CloudFile clientFile) {
        File cloudFile = new File(storageRootDirPath + "/" + clientFile.getName());
        if (!cloudFile.exists()) {
            try {
                if (!cloudFile.createNewFile()) return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long fileLength = clientFile.getFileLength();
        try {
            System.out.println("Uploading...");
            FileOutputStream fos = new FileOutputStream(cloudFile);
            long numberOfSends = fileLength / bufferSize;
            for (long i = 0; i <= numberOfSends; i++) {
                int bytesRead = is.read(buffer);
                System.out.print("\r" + i + "/" + numberOfSends);
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
            fos.close();
            System.out.println("\nFile uploaded");
            connectionHandler.sendResponse(Responses.OK.getString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean getFileFromStorage(CloudFile file) {
        if (file.exists()) {
            long fileLength = file.length();
            try {
                connectionHandler.sendResponse(String.valueOf(file.length()));
                FileInputStream fis = new FileInputStream(file);
                long numberOfSends = fileLength / bufferSize;
                for (long i = 0; i <= numberOfSends; i++) {
                    int byteRead = fis.read(buffer);
                    os.write(buffer, 0, byteRead);
                    os.flush();
                }
                System.out.println("File sent");
                fis.close();
                Thread.sleep(50); // todo Костыль. Надо найти другое решение. Без этого в передачу файла попадает ответ сервера
                connectionHandler.sendResponse(Responses.OK.getString());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
