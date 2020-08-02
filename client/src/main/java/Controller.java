import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    public Button send;

    @FXML
    public ListView<String> localFileListView;

    @FXML
    public ListView<String> storageFileListView;

    @FXML
    public TextField text;

    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    private ClientFileHandler fileHandler;

    public Controller() {
        try {
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileHandler = new ClientFileHandler(this);
        try{
            for (File file : fileHandler.getClientFileList()) {
                localFileListView.getItems().add(file.getName());
            }
            getStorageDirContent();

            localFileListView.setOnMouseClicked(a -> {
                if (a.getClickCount() == 2) {
                    String fileName = localFileListView.getSelectionModel().getSelectedItem();
                    fileHandler.uploadFile(fileName);
                    if (isResponseOk())
                    getStorageDirContent();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getStorageDirContent() {
        List<File> storageDirContent = fileHandler.getStorageDirContent();
        storageFileListView.getItems().clear();
        for (File file : storageDirContent) {
            storageFileListView.getItems().add(file.getName());
        }
    }

    public void handleCommandAction(ActionEvent actionEvent) {
        String command = text.getCharacters().toString();
        if (command.startsWith(Commands.DOWNLOAD.getString())){
            fileHandler.downLoadFile(command);
        }
        if (command.startsWith(Commands.CLOSE_CONNECTION.getString())){
            sendCommand(Commands.CLOSE_CONNECTION.getString());
        }
    }

    public void sendCommand(String command){
        try {
            System.out.println(command);
            os.writeBytes(command+Main.END_COMMAND_CHAR);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStringFromServer() {
        StringBuilder stringFromServer= new StringBuilder();
        char b = 0;
        try {
            while (true) {
                b = (char) is.readByte();
                if (b != '|') {
                    stringFromServer.append(b);
                } else {
                    System.out.println(stringFromServer);
                    return stringFromServer.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringFromServer.toString();
    }

    public boolean isResponseOk() {
        String response = getStringFromServer();
        return response.equals(Responses.OK.getString());
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getIs() {
        return is;
    }

    public DataOutputStream getOs() {
        return os;
    }
}
