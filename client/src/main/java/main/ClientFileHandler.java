package main;

import commands.Requests;
import commands.Responses;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClientFileHandler {

    public static final String PARENT_DIR_MARK = "..";

    private MainController controller;
    private DataInputStream is;
    private DataOutputStream os;
    private List<CloudFile> clientFileList;
    private List<CloudFile> currentStorageDirFileList;
    private String clientDirPath;
    private String currentStorageDirName;
    private CloudFile currentClientDir;
    private CloudFile rootClientDir;

    public ClientFileHandler(MainController controller) {
        this.controller = controller;
        this.is = controller.getIs();
        this.os = controller.getOs();
        this.clientDirPath = Main.CLIENT_DIR_PATH;
        this.rootClientDir = new CloudFile(clientDirPath);
        if (!rootClientDir.exists()) {
            if (rootClientDir.mkdir()) {
                System.out.println("Создана корневая папка пользователя ");
            } else {
                throw new RuntimeException("Корневая папка пользователя отсутствует");
            }
        }
        this.currentStorageDirName = "";
        currentStorageDirFileList = new ArrayList<>();
        this.currentClientDir = new CloudFile(clientDirPath);
        clientFileList = getClientFileList();
    }

    public List<CloudFile> getClientFileList() {
        clientFileList = new ArrayList<>();
        for (File file : Objects.requireNonNull(currentClientDir.listFiles())) {
            CloudFile cloudFile = new CloudFile(file.getAbsolutePath(), file.isDirectory());
            clientFileList.add(cloudFile);
        }
        return clientFileList;
    }

    public List<CloudFile> getStorageDirContent() {

        controller.sendCommand(Requests.GET_DIR_CONTENT);
        if (controller.isResponseOk()) {
            try {
                os.write((byte)currentStorageDirName.length());
                System.out.println(currentStorageDirName.length() + " was sent");
                os.write(currentStorageDirName.getBytes(), 0, currentStorageDirName.length());
                System.out.println(currentStorageDirName + " was sent");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFilesFromStorage();
    }

    private List<CloudFile> getFilesFromStorage() {
        currentStorageDirFileList.clear();
        while (true){
            try {

                byte signalByte = is.readByte();

                System.out.println(signalByte);
                int fileNameLength;
                if (signalByte == Responses.END_OF_DIR_CONTENT.getSignalByte())
                    break;
                else {
                    fileNameLength = signalByte;
                    System.out.println("Signal byte: " + signalByte);

                    byte[] fName = new byte[fileNameLength];

                    for (int i = 0; i < fileNameLength; i++) {
                        byte b = is.readByte();
                        fName[i] = b;
                    }

                    String fileName = new String(fName);
                    System.out.println(fileName);

                    byte fType = is.readByte();
                    boolean isDirectory = (char) fType == 'D';
                    System.out.println(isDirectory);

                    long fileSize = is.readLong();
                    System.out.println(fileSize);
                    currentStorageDirFileList.add(new CloudFile(fileName, isDirectory, fileSize));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return currentStorageDirFileList;
    }

    public List<CloudFile> getSharedDirContent() {
        controller.sendCommand(Requests.GET_SHARED_DIR_CONTENT);
        if (controller.isResponseOk()) {
            return getFilesFromStorage();
        }
        return null;
    }

    public void downLoadFile(String downloadedFileName) {
        String downloadedFileFullName = currentClientDir + "/" + downloadedFileName;
        try {
            controller.sendCommand(Requests.DOWNLOAD);
            if (controller.isResponseOk()) {
                controller.sendString(downloadedFileName);
                if (controller.isResponseOk()) {
                    String fileLengthStr = controller.getStringFromServer();
                    long downloadedFileSize = Long.parseLong(fileLengthStr);
                    putFileIntoUserDir(downloadedFileFullName, downloadedFileSize);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void putFileIntoUserDir(String downloadedFileFullName, long downloadedFileSize) throws IOException {
        File downloadedFile = new File(downloadedFileFullName);
        if (!downloadedFile.exists()) {
            downloadedFile.createNewFile();
        }
        int bufferSize = Main.BUFFER_SIZE;
        byte[] buffer = new byte[bufferSize];
        try {
            FileOutputStream fos = new FileOutputStream(downloadedFile);
            if (downloadedFileSize > 0) {
                long numberOfSends = downloadedFileSize / bufferSize;
                for (long i = 0; i <= numberOfSends; i++) {
                    int bytesRead = is.read(buffer);
                    fos.write(buffer, 0, bytesRead);
                    fos.flush();
                }
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteStorageFile(String fileName) {
        controller.sendCommand(Requests.DELETE);
        if (controller.isResponseOk()) {
            controller.sendString(fileName);
        }
    }

    public void deleteLocalFile(String fileName) {
        CloudFile file = findFileByName(fileName);
        if (file != null) {
            recursiveDelete(file);
        }
    }

    private void recursiveDelete(File file) {
        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                recursiveDelete(f);
            }
        }

        file.delete();
    }

    public void downLoadSharedFile(String fullPathFileName) {
        String[] unixFileNameArray = fullPathFileName.split("/");
        String[] windowsFileNameArray = unixFileNameArray[unixFileNameArray.length - 1].split("\\\\");
        String downloadedFileName = windowsFileNameArray[windowsFileNameArray.length - 1];
        String downloadedFileFullName = currentClientDir + "/" + downloadedFileName;
        try {
            controller.sendCommand(Requests.DOWNLOAD);
            if (controller.isResponseOk()) {
                controller.sendString(fullPathFileName);
                if (controller.isResponseOk()) {
                    String fileLengthStr = controller.getStringFromServer();
                    long downloadedFileSize = Long.parseLong(fileLengthStr);
                    putFileIntoUserDir(downloadedFileFullName, downloadedFileSize);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(String fileName) {
        CloudFile currentFile = findFileByName(fileName);
        if (currentFile != null) {
            try {
                controller.sendCommand(Requests.UPLOAD);
                if (controller.isResponseOk()) {
                    os.writeByte(currentFile.getName().length());
                    System.out.println("-> " + currentFile.getName().length());
                    os.writeBytes(fileName);
                    System.out.println("-> " + currentFile.getName());
                    long fileLength = currentFile.length();
                    os.writeLong(fileLength);
                    System.out.println("-> " + fileLength);

                } else {
                    return;
                }
//                if (controller.isResponseOk()) {
                    sendFile(currentFile);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private CloudFile findFileByName(String fileName) {
        for (CloudFile file : clientFileList) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public void sendFile(CloudFile currentFile) throws IOException {
        FileInputStream fis = new FileInputStream(currentFile);
        int bytesRead = 0;
        byte[] buffer = new byte[Main.BUFFER_SIZE];
        while (fis.available() > 0) {
            bytesRead = fis.read(buffer);
            os.write(buffer, 0, bytesRead);
            os.flush();
        }
        fis.close();
    }

    public void openLocalDir(String fileName) {
        CloudFile f;
        if (fileName.equals(PARENT_DIR_MARK)) {
            f = new CloudFile(currentClientDir.getParent(), true);
        } else {
            f = findFileByName(fileName);
        }
        if (f != null && f.isDirectory()) {
            currentClientDir = new CloudFile(f.getAbsolutePath());
        }
    }

    public void openStorageDir(String fileName) {
        currentStorageDirName = fileName;
    }

    public CloudFile getCurrentClientDir() {
        return currentClientDir;
    }

    public CloudFile getRootClientDir() {
        return rootClientDir;
    }

    public String getCurrentStorageDirName() {
        return currentStorageDirName;
    }
}
