import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileHandler {

    private Controller controller;
    private InputStream is;
    private OutputStream os;
    private List<File> clientFileList;
    private String clientDirPath;
    private File clientDir;

    public FileHandler(Controller controller) {
        this.controller = controller;
        this.is = controller.getIs();
        this.os = controller.getOs();
        this.clientDirPath = Main.CLIENT_DIR_PATH;
        clientFileList = getClientFileList();
    }

    public List<File> getClientFileList() {
        clientFileList = new ArrayList<>();
        clientDir = new File(clientDirPath);
        if (!clientDir.exists()) {
            throw new RuntimeException("directory resource not exists on client");
        }
        for (File file : Objects.requireNonNull(clientDir.listFiles())) {
            clientFileList.add(file);
        }
        return clientFileList;
    }

    public void downLoadFile(String commandToDownloadFile) {
        try {
            String[] commands = commandToDownloadFile.split(" ");
            String downloadCommand = commands[0];
            String downloadedFileName = commands[1];
            controller.sendCommand(downloadCommand);
            if (controller.isResponseOk()) {
                controller.sendCommand(downloadedFileName);

                if (controller.isResponseOk()) {
                    String downloadedFileFullName = clientDir + "/" + downloadedFileName;

                    String fileLengthStr = controller.getStringFromServer();
                    long downloadedFileSize = Long.parseLong(fileLengthStr);
                    File downloadedFile = new File(downloadedFileFullName);
                    if (!downloadedFile.exists()) {
                        downloadedFile.createNewFile();
                    }
                    int bufferSize = Main.BUFFER_SIZE;
                    byte[] buffer = new byte[bufferSize];
                    try {
                        System.out.println("Получение файла");
                        FileOutputStream fos = new FileOutputStream(downloadedFile);
                        for (long i = 0; i < (downloadedFileSize / bufferSize == 0 ? 1 : downloadedFileSize / bufferSize); i++) {
                            int bytesRead = is.read(buffer);
                            fos.write(buffer, 0, bytesRead);
                            fos.flush();
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
        File currentFile = findFileByName(fileName);
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

    private File findFileByName(String fileName) {
        for (File file : clientFileList) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public void sendFile(File currentFile) throws IOException {
        FileInputStream fis = new FileInputStream(currentFile);

        byte[] buffer = new byte[Main.BUFFER_SIZE];
        while (fis.available() > 0) {
            int bytesRead = fis.read(buffer);
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        fis.close();
        System.out.println("File sent");
    }
}
