import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    public Button send;
    public ListView<String> listView;
    public TextField text;
    private List<File> clientFileList;
    public static Socket socket;
    private DataInputStream is;
    private static DataOutputStream os;
    private File clientDir;

    public void downloadFileAction(ActionEvent actionEvent) {
        String fileName = text.getCharacters().toString();
        downLoadFile(fileName);
    }

    public void downLoadFile(String fileName){
        try {
            os.writeUTF("./download");
            os.writeUTF(fileName);
            String response = is.readUTF();
            if (response.equals("./take")){
                String downloadedFileName = clientDir + "/" + is.readUTF();
                System.out.println(downloadedFileName);
                long downloadedFileSize = is.readLong();
                File downloadedFile = new File(downloadedFileName);
                if (!downloadedFile.exists()) {
                    if (!downloadedFile.createNewFile());
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
                    os.writeUTF("Ok");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread.sleep(1000);
            clientFileList = new ArrayList<>();
            String clientPath = "./client/src/main/resources/";
            clientDir = new File(clientPath);
            if (!clientDir.exists()) {
                throw new RuntimeException("directory resource not exists on client");
            }
            for (File file : Objects.requireNonNull(clientDir.listFiles())) {
                clientFileList.add(file);
                listView.getItems().add(file.getName());
            }
            listView.setOnMouseClicked(a -> {
                if (a.getClickCount() == 2) {
                    String fileName = listView.getSelectionModel().getSelectedItem();
                    File currentFile = findFileByName(fileName);
                    if (currentFile != null) {
                        try {
                            os.writeUTF("./upload");
                            os.writeUTF(fileName);
                            os.writeLong(currentFile.length());
                            FileInputStream fis = new FileInputStream(currentFile);
                            byte [] buffer = new byte[1024];
                            while (fis.available() > 0) {
                                int bytesRead = fis.read(buffer);
                                os.write(buffer, 0, bytesRead);
                            }
                            os.flush();
                            String response = is.readUTF();
                            System.out.println(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File findFileByName(String fileName) {
        for (File file : clientFileList) {
            if (file.getName().equals(fileName)){
                return file;
            }
        }
        return null;
    }

    public static void closeClient(){
        try {
            os.writeUTF("/close");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
