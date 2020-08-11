
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientFileHandler {

    public static final String DIR_MARK = "./D";
    public static final String FILE_MARK = "./F";
    public static final String PARENT_DIR_MARK = "..";

    private Controller controller;
    private InputStream is;
    private OutputStream os;
    private List<CloudFile> clientFileList;
    private List<CloudFile> currentStorageDirFileList;
    private String clientDirPath;
    private String currentStorageDirName;
    private CloudFile currentClientDir;
    private CloudFile rootClientDir;

    public ClientFileHandler(Controller controller) {
        this.controller = controller;
        this.is = controller.getIs();
        this.os = controller.getOs();
        this.clientDirPath = Main.CLIENT_DIR_PATH;
        this.rootClientDir = new CloudFile(clientDirPath);
        if (!rootClientDir.exists()) {
            if (rootClientDir.mkdir()) {
                System.out.println("Создана корневая папка пользователя ");
            } else {
                throw new RuntimeException("directory resource not exists on client");
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

        controller.sendCommand(Commands.GET_DIR_CONTENT.getString());
        if (controller.isResponseOk()){
            controller.sendCommand(currentStorageDirName);
        }
        return getFilesFromStorage();
    }

    private List<CloudFile> getFilesFromStorage() {
        currentStorageDirFileList.clear();
        while (true){
            String f = controller.getStringFromServer();
            String fileName = "";
            CloudFile cloudFile;
            if (f.equals(Responses.END_OF_DIR_CONTENT.getString())) break;
            if (f.startsWith(DIR_MARK)){
                fileName=f.substring(DIR_MARK.length());
                cloudFile = new CloudFile(fileName, true);
            } else if(f.startsWith(FILE_MARK)) {
                fileName=f.substring(FILE_MARK.length());
                cloudFile = new CloudFile(fileName, false);
            } else {
                cloudFile = new CloudFile(fileName);
            }
            currentStorageDirFileList.add(cloudFile);
        }
        return currentStorageDirFileList;
    }

    public List<CloudFile> getSharedDirContent() {
        controller.sendCommand(Commands.GET_SHARED_DIR_CONTENT.getString());
        if (controller.isResponseOk()){
            return getFilesFromStorage();
        }
        return null;
    }

    public void downLoadFile(String downloadedFileName) {
        String downloadedFileFullName = currentClientDir + "/" + downloadedFileName;
        downloadFile(downloadedFileName);
    }

    private void downloadFile(String fileName) {
        try {
            controller.sendCommand(Commands.DOWNLOAD.getString());
            if (controller.isResponseOk()) {
                controller.sendCommand(fileName);
                if (controller.isResponseOk()) {
                    String fileLengthStr = controller.getStringFromServer();
                    long downloadedFileSize = Long.parseLong(fileLengthStr);
                    System.out.println(fileName);

                    File downloadedFile = new File(fileName);
                    if (!downloadedFile.exists()) {
                        downloadedFile.createNewFile();
                    }
                    int bufferSize = Main.BUFFER_SIZE;
                    byte[] buffer = new byte[bufferSize];
                    try {
                        FileOutputStream fos = new FileOutputStream(downloadedFile);
                        if (downloadedFileSize>0) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(String fileName) {
        CloudFile currentFile = findFileByName(fileName);
        if (currentFile != null) {
            try {
                controller.sendCommand(Commands.UPLOAD.getString());
                if (controller.isResponseOk()) {
                    controller.sendCommand(fileName);
                } else {
                    return;
                }
                if (controller.isResponseOk()) {
                    long fileLength = currentFile.length();
                    String fileLengthStr = String.valueOf(fileLength);
                    controller.sendCommand(fileLengthStr);
                } else {
                    return;
                }
                if (controller.isResponseOk()) {
                    sendFile(currentFile);
                }
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
        int bytesRead=0;
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
        if (fileName.equals(PARENT_DIR_MARK)){
            f = new CloudFile(currentClientDir.getParent(), true);
        } else {
            f = findFileByName(fileName);
        }
        if (f!=null&&f.isDirectory()){
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
