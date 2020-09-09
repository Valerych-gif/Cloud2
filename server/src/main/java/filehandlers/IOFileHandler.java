package filehandlers;

import commands.Responses;
import entities.CloudFile;
import entities.User;
import network.Network;
import settings.Cloud2ServerSettings;

import java.io.*;


public class IOFileHandler extends FileHandler {
    public IOFileHandler(User user, Network network) {
        super();
        this.network=network;
        this.storageRootDirPath = user.getUserStorage().getAbsolutePath();
        this.rootStorageDir = new CloudFile(storageRootDirPath);
        this.currentStorageDir = rootStorageDir;
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
                    int bytesRead = network.readBufferFromClient(buffer);
                    System.out.print("\r" + i + "/" + numberOfSends);
                    fos.write(buffer, 0, bytesRead);
                }
                fos.flush();
            }
            fos.close();
            System.out.println("\nFile uploaded");
            network.sendResponse(Responses.OK.getString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setCurrentStorageDir(String fileName) {
        String newFileName;
        CloudFile file;
        if (fileName.equals(IOFileHandler.PARENT_DIR_MARK)) {
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
            network.sendResponse(Responses.OK.getString());
            long fileLength = file.length();
            try {
                network.sendResponse(String.valueOf(file.length()));
                if (fileLength > 0) {
                    FileInputStream fis = new FileInputStream(file);
                    long numberOfSends = fileLength / bufferSize;
                    for (long i = 0; i <= numberOfSends; i++) {
                        int byteRead = fis.read(buffer);
                        network.sendBufferToClient(buffer, byteRead);// TODO callback.execute()
                    }
                    fis.close();
                }
                Thread.sleep(50); // todo Костыль. Надо найти другое решение. Без этого в передачу файла попадает ответ сервера
                network.sendResponse(Responses.OK.getString());
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
