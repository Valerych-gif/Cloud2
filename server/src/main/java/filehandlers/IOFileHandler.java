package filehandlers;

import commands.Responses;
import connectionhandlers.IOConnectionHandler;
import entities.CloudFile;
import connectionhandlers.ConnectionHandler;
import settings.Cloud2ServerSettings;

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

    public boolean loadFileToStorage(CloudFile clientFile) {
        File cloudFile = getAbsFilePathByName(clientFile.getName());
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

    public void deleteFileFromStorage(String fileName){
        File file = new File(currentStorageDir.getAbsolutePath() + "/" + fileName);
        recursiveDelete(file);
    }

    private void recursiveDelete(File file) {
        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }

        file.delete();
    }

    public File getAbsFilePathByName(String fileName){
        return new File(currentStorageDir.getAbsolutePath() + "/" + fileName);
    }

    public boolean getFileFromStorage(String fileName) {
        CloudFile file = new CloudFile(currentStorageDir.getAbsolutePath() + "/" + fileName);
        if (!file.exists()) file = new CloudFile(Cloud2ServerSettings.STORAGE_ROOT_DIR + "/" + fileName);
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
            logger.error("Неправильное имя файла");
        }
        return false;
    }
}
