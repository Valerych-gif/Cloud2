package files;

import main.Cloud2ServerStarter;
import main.ConnectionHandler;

import java.io.*;
import java.util.Arrays;

public class FileHandler {

    private byte [] buffer;

    private ConnectionHandler connectionHandler;
    private DataInputStream is;
    private DataOutputStream os;
    private int bufferSize;
    private String storageRootDir;


    public FileHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.buffer = new byte[Cloud2ServerStarter.BUFFER_SIZE];
        this.is = connectionHandler.getDataInputStream();
        this.os = connectionHandler.getDataOutputStream();
        this.bufferSize = Cloud2ServerStarter.BUFFER_SIZE;
        this.storageRootDir = Cloud2ServerStarter.storageRootDir;
    }

    public boolean loadFileToStorage(CloudFile clientFile) throws IOException {
        File cloudFile = new File( storageRootDir + "/" + clientFile.getName());
        if (!cloudFile.exists()) {
            if (!cloudFile.createNewFile()) return false;
        }
        long fileLength = clientFile.getFileLength();
        try {
            System.out.println("Получение файла");
            FileOutputStream fos = new FileOutputStream(cloudFile);
            for (long i = 0; i < (fileLength / bufferSize == 0 ? 1 : fileLength / bufferSize); i++) {
                int bytesRead = is.read(buffer);
                fos.write(buffer, 0, bytesRead);
            }
            os.writeUTF("Ok");
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean getFileFromStorage(CloudFile file) {
        if (file.exists()){
            String fileName = file.getName();
            long fileLength = file.length();
            try {
                System.out.println("Отправка файла");
                os.writeUTF("./take");
                os.writeUTF(fileName);
                os.writeLong(file.length());
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
