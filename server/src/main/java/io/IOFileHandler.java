package io;

import files.CloudFile;
import main.Cloud2ServerStarter;
import main.FileHandler;
import main.ConnectionHandler;
import main.Responses;

import java.io.*;

public class IOFileHandler extends FileHandler {
    private DataInputStream is;
    private DataOutputStream os;

    public IOFileHandler(ConnectionHandler connectionHandler) {
        super(connectionHandler);
        init();
    }

    @Override
    public void init() {
        this.is = connectionHandler.getDataInputStream();
        this.os = connectionHandler.getDataOutputStream();
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
            FileOutputStream fos = new FileOutputStream(cloudFile);
            for (long i = 0; i < (fileLength / bufferSize == 0 ? 1 : fileLength / bufferSize); i++) {
                int bytesRead = is.read(buffer);
                fos.write(buffer, 0, bytesRead);
            }
            connectionHandler.sendResponse(Responses.OK.getString());
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
