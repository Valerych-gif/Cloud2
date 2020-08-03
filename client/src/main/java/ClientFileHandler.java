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
    private String currentStorageDir;
    private CloudFile currentClientDir;
    private CloudFile rootClientDir;

    public ClientFileHandler(Controller controller) {
        this.controller = controller;
        this.is = controller.getIs();
        this.os = controller.getOs();
        this.clientDirPath = Main.CLIENT_DIR_PATH;
        this.rootClientDir = new CloudFile(clientDirPath);
        this.currentStorageDir = "/";
        currentStorageDirFileList = new ArrayList<>();
        this.currentClientDir = new CloudFile(clientDirPath);
        clientFileList = getClientFileList();

        if (!rootClientDir.exists()) {
            throw new RuntimeException("directory resource not exists on client");
        }
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
            controller.sendCommand(currentStorageDir);
        }

        currentStorageDirFileList.clear();
        while (true){
            String f = controller.getStringFromServer();
            String fileName = "";
            CloudFile cloudFile;
            if (f.equals("./endofdircontent")) break;
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

    public void downLoadFile(String downloadedFileName) {
        try {

            controller.sendCommand(Commands.DOWNLOAD.getString());
            if (controller.isResponseOk()) {
                controller.sendCommand(downloadedFileName);

                if (controller.isResponseOk()) {
                    String downloadedFileFullName = rootClientDir + "/" + downloadedFileName;

                    String fileLengthStr = controller.getStringFromServer();
                    long downloadedFileSize = Long.parseLong(fileLengthStr);
                    File downloadedFile = new File(downloadedFileFullName);
                    if (!downloadedFile.exists()) {
                        downloadedFile.createNewFile();
                    }
                    int bufferSize = Main.BUFFER_SIZE;
                    byte[] buffer = new byte[bufferSize];
                    try {
                        System.out.println("Downloading...");
                        FileOutputStream fos = new FileOutputStream(downloadedFile);
                        long numberOfSends = downloadedFileSize / bufferSize;
                        for (long i = 0; i <= numberOfSends; i++) {
                            System.out.print("\r" + i + "/" + numberOfSends);
                            int bytesRead = is.read(buffer);
                            fos.write(buffer, 0, bytesRead);
                            fos.flush();
                        }
                        fos.close();
                        System.out.println("\nFile downloaded");
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
                }
                if (controller.isResponseOk()) {
                    long fileLength = currentFile.length();
                    String fileLengthStr = String.valueOf(fileLength);
                    controller.sendCommand(fileLengthStr);
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
        System.out.println("File sent");
    }

    public void openDir(String fileName) {
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

    public CloudFile getCurrentClientDir() {
        return currentClientDir;
    }

    public CloudFile getRootClientDir() {
        return rootClientDir;
    }
}
