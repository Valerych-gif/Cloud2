import java.io.*;
import java.util.List;

public class FileHandler {

    private Controller controller;
    private InputStream is;
    private OutputStream os;
    private String clientDir;
    private List<File> clientFileList;

    public FileHandler(Controller controller) {
        this.controller = controller;
        this.is = controller.getIs();
        this.os = controller.getOs();
        this.clientDir = Main.storageRootDir;
        clientFileList = controller.getClientFileList();
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
                    int bufferSize = 1024;
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


    }public void uploadFile(String fileName) {
        File currentFile = findFileByName(fileName);
        if (currentFile != null) {
            try {
                controller.sendCommand(Commands.UPLOAD.getString());
                os.flush();
                if (controller.isResponseOk()) {
                    controller.sendCommand(fileName);
                    os.flush();
                }
                if (controller.isResponseOk()) {
                    long fileLength = currentFile.length();
                    String fileLengthStr = String.valueOf(fileLength);
                    controller.sendCommand(fileLengthStr);
                    os.flush();
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
            System.out.println(file.getName());
            if (file.getName().equals(fileName)){
                return file;
            }
        }
        return null;
    }

    public void sendFile(File currentFile) throws IOException {
        FileInputStream fis = new FileInputStream(currentFile);

        byte [] buffer = new byte[1024];
        while (fis.available() > 0) {
            int bytesRead = fis.read(buffer);
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
    }
}
