package files;

import io.IOConnectionHandler;
import main.ConnectionHandler;

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
        this.is = ((IOConnectionHandler) connectionHandler).getDataInputStream();
        this.os = ((IOConnectionHandler) connectionHandler).getDataOutputStream();
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
