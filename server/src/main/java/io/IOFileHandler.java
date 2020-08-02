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
        this.is = ((IOConnectionHandler)connectionHandler).getDataInputStream();
        this.os = ((IOConnectionHandler)connectionHandler).getDataOutputStream();
    }

    public void sendDirContentToClient(){
        String currentDir = connectionHandler.getStringFromClient();
        currentDir = Cloud2ServerStarter.STORAGE_ROOT_DIR + currentDir;
        File file = new File(currentDir);

        for (File f : Objects.requireNonNull(file.listFiles())) {
            connectionHandler.sendResponse(f.getName());
        }

        connectionHandler.sendResponse(Responses.END_OF_DIR_CONTENT.getString());
    }

    public boolean loadFileToStorage(CloudFile clientFile){
        File cloudFile = new File( storageRootDirPath + "/" + clientFile.getName());
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
            long numberOfSends = fileLength / bufferSize + 1;
            for (long i = 0; i < numberOfSends; i++) {
                int bytesRead = is.read(buffer);
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            connectionHandler.sendResponse(Responses.OK.getString());
            System.out.println("File uploaded");
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean getFileFromStorage(CloudFile file) {
        if (file.exists()){
            long fileLength = file.length();
            try {
                os.writeBytes(String.valueOf(file.length())+ Cloud2ServerStarter.END_COMMAND_CHAR);
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[bufferSize];
                for (long i = 0; i < (fileLength / bufferSize == 0 ? 1 : fileLength / bufferSize); i++) {
                    int byteRead = fis.read(buffer);
                    os.write(buffer, 0, byteRead);
                }
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
