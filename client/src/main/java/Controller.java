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

    @FXML
    public Button refreshListsButton;

    @FXML
    public Button copyFileButton;

    @FXML
    public Button moveFileButton;

    @FXML
    public Button deleteFileButton;

    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    private ClientFileHandler fileHandler;
    private String activeFile;
    private final String STORAGE_PANEL = "storage_panel";
    private final String LOCAL_PANEL = "local_panel";
    private String activePanel;

    public Controller() {
        try {
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            activeFile=null;
            activePanel=STORAGE_PANEL;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileHandler = new ClientFileHandler(this);
        try{
            refreshClientDirContent();
            refreshStorageDirContent();

            localFileListView.setOnMouseClicked(a -> {

                if (a.getClickCount() == 2) {
                    String fileName = localFileListView.getSelectionModel().getSelectedItem();
                    fileHandler.openLocalDir(fileName);
                    refreshClientDirContent();
                } else {
                    activeFile = localFileListView.getSelectionModel().getSelectedItem();
                    activePanel = LOCAL_PANEL;
                }
            });

            storageFileListView.setOnMouseClicked(a -> {
                if (a.getClickCount() == 2) {
                    String fileName = storageFileListView.getSelectionModel().getSelectedItem();
                    fileHandler.openStorageDir(fileName);
                    refreshStorageDirContent();

                }else{
                    activeFile = storageFileListView.getSelectionModel().getSelectedItem();
                    activePanel = STORAGE_PANEL;
                }
            });

            refreshListsButton.setOnAction(a->{
                refreshStorageDirContent();
                refreshClientDirContent();
            });

            copyFileButton.setOnAction(a->{
                String fileName="";
                if (activeFile != null) {
                    fileName = activeFile;
                    if (activePanel.equals(STORAGE_PANEL)){
                        fileHandler.downLoadFile(fileName);
                        if (isResponseOk())
                            refreshClientDirContent();
                    } else if (activePanel.equals(LOCAL_PANEL)){
                        fileHandler.uploadFile(fileName);
                        if (isResponseOk())
                            refreshStorageDirContent();
                    }
                }
            });

            moveFileButton.setOnAction(a->{
                System.out.println("move");
            });

            deleteFileButton.setOnAction(a->{
                System.out.println("delete");
            });

            send.setOnAction(a->{
                String command = text.getCharacters().toString();
                sendCommand(command);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshClientDirContent() {
        List<CloudFile> clientDirContent = fileHandler.getClientFileList();
        localFileListView.getItems().clear();
        if (!fileHandler.getCurrentClientDir().getAbsolutePath().equals(fileHandler.getRootClientDir().getAbsolutePath()))
            localFileListView.getItems().add(ClientFileHandler.PARENT_DIR_MARK);

        for (CloudFile file : clientDirContent) {
            localFileListView.getItems().add(file.getName());
        }
    }

    public void refreshStorageDirContent() {
        List<CloudFile> storageDirContent = fileHandler.getStorageDirContent();
        storageFileListView.getItems().clear();
        for (CloudFile file : storageDirContent) {
            storageFileListView.getItems().add(file.getName());
        }
    }


    public void sendCommand(String command){
        try {
            System.out.println("->\t" + command);
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
                    System.out.println("<-\t" + stringFromServer);
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
