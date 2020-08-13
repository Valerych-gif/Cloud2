package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modalWindows.ShareModalWindow;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class mainController implements Initializable {

    @FXML
    public ListView<String> localFileListView;

    @FXML
    public ListView<String> storageFileListView;

    @FXML
    public TextField loginField;

    @FXML
    public TextField passField;

    @FXML
    public Button regButton;

    @FXML
    public Button signInButton;

    @FXML
    public Button refreshListsButton;

    @FXML
    public Button swapButton;

    @FXML
    public Button copyFileButton;

    @FXML
    public Button moveFileButton;

    @FXML
    public Button deleteFileButton;

    @FXML
    public Button shareButton;

    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    private ClientFileHandler fileHandler;
    private String activeFile;
    private final String STORAGE_PANEL = "storage_panel";
    private boolean inSharedFileMode = false;
    private final String LOCAL_PANEL = "local_panel";
    private String activePanel;

    private String login;
    private String pass;

    public mainController() {
        try {
            socket = new Socket(Main.SERVER, Main.PORT);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            activeFile = null;
            activePanel = STORAGE_PANEL;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        fileHandler = new ClientFileHandler(this);
        try {
            refreshClientDirContent();

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

                } else {
                    activeFile = storageFileListView.getSelectionModel().getSelectedItem();
                    activePanel = STORAGE_PANEL;
                }
            });

            refreshListsButton.setOnAction(a -> {
                refreshStorageDirContent();
                refreshClientDirContent();
            });

            swapButton.setOnAction(a->{
                if (inSharedFileMode){
                    refreshStorageDirContent();
                    inSharedFileMode = false;
                } else {
                    getSharedDirContent();
                    inSharedFileMode = true;
                }
            });

            copyFileButton.setOnAction(a -> {
                String fileName = "";
                if (activeFile != null) {
                    fileName = activeFile;
                    if (activePanel.equals(STORAGE_PANEL)) {
                        if (!inSharedFileMode) {
                            fileHandler.downLoadFile(fileName);
                        } else {
                            fileHandler.downLoadSharedFile(fileName);
                        }
                        if (isResponseOk())
                            refreshClientDirContent();
                    } else if (activePanel.equals(LOCAL_PANEL)&&!inSharedFileMode) {
                        fileHandler.uploadFile(fileName);
                        if (isResponseOk())
                            refreshStorageDirContent();
                    }
                }
            });

            moveFileButton.setOnAction(a -> {
                System.out.println("move");
            });

            deleteFileButton.setOnAction(a -> {
                System.out.println("delete");
            });

            shareButton.setOnAction(a -> {
                ShareModalWindow shareModalWindow = new ShareModalWindow(this);
                try {
                    shareModalWindow.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            signInButton.setOnAction(a -> {
                authorization();
            });

            regButton.setOnAction(a -> {
                registration();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void authorization() {
        sendCommand(Commands.AUTHORIZATION.getString());
        confirmAuthorization();
    }

    private void confirmAuthorization() {
        String l = null, p = null;
        if (isResponseOk()) {
            l = loginField.getText();
            p = passField.getText();
            sendCommand(l + " " + p);
        }
        if (isResponseOk()) {
            refreshStorageDirContent();
            this.login = l;
            this.pass = p;
        }
    }

    public void registration() {
        sendCommand(Commands.REGISTRATION.getString());
        confirmAuthorization();
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

    private void getSharedDirContent() {
        List<CloudFile> storageDirContent = fileHandler.getSharedDirContent();
        storageFileListView.getItems().clear();
        for (CloudFile file : storageDirContent) {
            storageFileListView.getItems().add(file.getPath());
        }
    }

    public void sendCommand(String command) {
        try {
            if (Main.DEBUG_MODE) System.out.println("->\t" + command);
            os.writeBytes(command + Main.END_COMMAND_CHAR);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStringFromServer() {
        StringBuilder stringFromServer = new StringBuilder();
        char b = 0;
        try {
            while (true) {
                b = (char) is.readByte();
                if (b != Main.END_COMMAND_CHAR) {
                    stringFromServer.append(b);
                } else {
                    if (Main.DEBUG_MODE) System.out.println("<-\t" + stringFromServer);
                    return stringFromServer.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringFromServer.toString();
    }

    public void shareFileByNickname(String nickName) {
        if (activePanel.equals(LOCAL_PANEL)) {
            sendCommand(Commands.SHARE.getString());
            if (isResponseOk()) {
                sendCommand(nickName);
                sendCommand(activeFile);
            }
        }
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