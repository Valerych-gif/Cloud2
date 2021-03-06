package ru.valerych.cloud2.client.controllers;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.entities.FileInfo;
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.client.network.ConnectionHandler;
import ru.valerych.cloud2.client.network.ConnectionObserver;
import ru.valerych.cloud2.client.services.fileservices.*;
import ru.valerych.cloud2.client.utils.Settings;
import ru.valerych.cloud2.client.windows.Cloud2Window;
import ru.valerych.cloud2.client.windows.WindowCreator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends WindowController implements Initializable, ConnectionObserver {
    private final Logger logger = LogManager.getLogger(MainWindowController.class.getName());

    public static final String DEFAULT_REMOTE_ROOT_DIRECTORY = "";
    public static final String DEFAULT_LOCAL_ROOT_DIRECTORY = "Cloud2Directory";
    public static final String LEFT_PANEL_REMOTE_CURRENT_DIRECTORY_PROPERTY = "left-panel-remote-current-directory";
    public static final String RIGHT_PANEL_REMOTE_CURRENT_DIRECTORY_PROPERTY = "right-panel-remote-current-directory";
    public static final String LEFT_PANEL_LOCAL_CURRENT_DIRECTORY_PROPERTY = "left-panel-local-current-directory";
    public static final String RIGHT_PANEL_LOCAL_CURRENT_DIRECTORY_PROPERTY = "right-panel-local-current-directory";

    private static final String ERROR_WINDOW = "/modalWindows/errorDownloadProcess.fxml";
    private static final String ABOUT_STAGE_TEMPLATE = "/modalWindows/aboutWindow.fxml";
    private static final String CONNECT_STAGE_TEMPLATE = "/modalWindows/connectWindow.fxml";
    private static final String REGISTRATION_STAGE_TEMPLATE = "/modalWindows/registrationWindow.fxml";
    private static final String SHARE_WINDOW = "/modalWindows/shareWindow.fxml";

    @FXML
    public Button refreshButton, copyButton, cutButton, pasteButton, moveButton, deleteButton, shareButton, connectButton, registrationButton, swapLeftPanelButton, swapRightPanelButton;

    @FXML
    public VBox mainPane, leftPanel, rightPanel;
    @FXML
    public MenuItem closeMenuItem;
    @FXML
    public MenuItem settingsMenuItem;
    @FXML
    public MenuItem aboutMenuItem;

    @FXML
    public TableColumn<FileInfo, String> nameColumnHeaderLeft, extColumnHeaderLeft, sizeColumnHeaderLeft, dateColumnHeaderLeft, attrColumnHeaderLeft;
    @FXML
    public TableColumn<FileInfo, String> nameColumnHeaderRight, extColumnHeaderRight, sizeColumnHeaderRight, dateColumnHeaderRight, attrColumnHeaderRight;
    @FXML
    public TextField addrTextFieldLeft;
    @FXML
    public TextField addrTextFieldRight;
    @FXML
    public TableView<FileInfo> leftFileTable, rightFileTable;

    private CloudConnection connection;
    private WindowCreator windowCreator;
    private LocalFileExplorer leftPanelLocalFileExplorer;
    private LocalFileExplorer rightPanelLocalFileExplorer;
    private RemoteFileExplorer leftPanelRemoteFileExplorer;
    private RemoteFileExplorer rightPanelRemoteFileExplorer;
    private boolean isLeftPanelRemote, isLeftPanelLocal, isLeftPanelShared;
    private boolean isRightPanelRemote, isRightPanelLocal, isRightPanelShared;
    private boolean isLeftPanelActive, isRightPanelActive;

    private final RemoteFileDownloader remoteFileDownloader;
    private final SharedFileDownloader sharedFileDownloader;
    private final FileUploader fileUploader;
    private final FileSharer fileSharer;
    private final SharedFileExplorer sharedFileExplorer;
    private final RemoteFileRemover remoteFileRemover;

    private final ConnectionHandler connectionHandler;

    public MainWindowController() {
        connectionHandler = new ConnectionHandler();
        connection = new CloudConnection();
        connection.setAuthorized(false);

        remoteFileDownloader = new RemoteFileDownloader();
        sharedFileDownloader = new SharedFileDownloader();
        fileUploader = new FileUploader();
        fileSharer = new FileSharer();
        sharedFileExplorer = new SharedFileExplorer();
        remoteFileRemover = new RemoteFileRemover();

        connectionHandler.registerObserver(this);
        connectionHandler.registerObserver(remoteFileDownloader);
        connectionHandler.registerObserver(sharedFileDownloader);
        connectionHandler.registerObserver(fileUploader);
        connectionHandler.registerObserver(fileSharer);
        connectionHandler.registerObserver(sharedFileExplorer);
        connectionHandler.registerObserver(remoteFileRemover);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        windowCreator = new WindowCreator();

        setUpLeftPanel();
        setUpRightPanel();
    }

    private void setUpRightPanel() {
        isRightPanelLocal = true;
        String remoteCurrentDirectoryProperty = Settings.read(RIGHT_PANEL_REMOTE_CURRENT_DIRECTORY_PROPERTY);
        String localCurrentDirectoryProperty = Settings.read(RIGHT_PANEL_LOCAL_CURRENT_DIRECTORY_PROPERTY);
        String rightPanelRemoteCurrentDirectory = remoteCurrentDirectoryProperty == null || "".equals(remoteCurrentDirectoryProperty) ? DEFAULT_REMOTE_ROOT_DIRECTORY : remoteCurrentDirectoryProperty;
        String rightPanelLocalCurrentDirectory = localCurrentDirectoryProperty == null || "".equals(localCurrentDirectoryProperty) ? "" : localCurrentDirectoryProperty;
        rightPanelLocalFileExplorer = new LocalFileExplorer(rightPanelLocalCurrentDirectory);
        rightPanelRemoteFileExplorer = new RemoteFileExplorer(rightPanelRemoteCurrentDirectory);
        connectionHandler.registerObserver(rightPanelRemoteFileExplorer);
        rightFileTable.setItems(rightPanelLocalFileExplorer.getFileList());
    }

    private void setUpLeftPanel() {
        isLeftPanelLocal = true;
        String remoteCurrentDirectoryProperty = Settings.read(LEFT_PANEL_REMOTE_CURRENT_DIRECTORY_PROPERTY);
        String localCurrentDirectoryProperty = Settings.read(LEFT_PANEL_LOCAL_CURRENT_DIRECTORY_PROPERTY);
        String leftPanelRemoteCurrentDirectory = remoteCurrentDirectoryProperty == null || "".equals(remoteCurrentDirectoryProperty) ? DEFAULT_REMOTE_ROOT_DIRECTORY : remoteCurrentDirectoryProperty;
        String leftPanelLocalCurrentDirectory = localCurrentDirectoryProperty == null || "".equals(localCurrentDirectoryProperty) ? "" : localCurrentDirectoryProperty;
        leftPanelLocalFileExplorer = new LocalFileExplorer(leftPanelLocalCurrentDirectory);
        leftPanelRemoteFileExplorer = new RemoteFileExplorer(leftPanelRemoteCurrentDirectory);
        connectionHandler.registerObserver(leftPanelRemoteFileExplorer);
        leftFileTable.setItems(leftPanelLocalFileExplorer.getFileList());
    }

    public void openSettings(ActionEvent actionEvent) {
        logger.debug("openSettings() " + actionEvent.toString());
    }

    public void openAboutProgram(ActionEvent actionEvent) {
        logger.debug("openAboutProgram() " + actionEvent.toString());
        windowCreator.createModalWindow("About", ABOUT_STAGE_TEMPLATE);
    }

    public void connectToServer(ActionEvent actionEvent) {
        logger.debug("connectToServer() " + actionEvent.toString());
        Cloud2Window window = windowCreator.createModalWindow("Connect", CONNECT_STAGE_TEMPLATE);
        ConnectWindowController connectWindowController = (ConnectWindowController) window.getController();
        connectWindowController.setConnectionHandler(connectionHandler);
        connectWindowController.show();
    }

    public void registration(ActionEvent actionEvent) {
        logger.debug("registration() " + actionEvent.toString());
        windowCreator.createModalWindow("Registration", REGISTRATION_STAGE_TEMPLATE);
    }

    @Override
    protected void show() {
        logger.debug("show() for main window");
        Stage primaryStage = (Stage) mainPane.getScene().getWindow();
        primaryStage.show();
    }

    @Override
    public void close() {
        logger.debug("close() for main window");
        Stage primaryStage = (Stage) mainPane.getScene().getWindow();
        primaryStage.close();
    }

    @FXML
    public void selectLeftTableRow(MouseEvent mouseEvent) {
        FileInfo fileInfo = getFileInfo(leftFileTable);
        if (mouseEvent.getClickCount() == 2 && fileInfo.isDirectory()) {
            String currentDirectory = fileInfo.getFileName();
            logger.debug("selectLeftTableRow() double click detected");
            if (isLeftPanelRemote) {
                if (!connection.isAuthorized()) return;
                leftPanelRemoteFileExplorer.setCurrentDirectory(currentDirectory);
                leftFileTable.setItems(leftPanelRemoteFileExplorer.getFileList());
            } else {
                leftPanelLocalFileExplorer.setCurrentDirectory(currentDirectory);
                Settings.write(LEFT_PANEL_LOCAL_CURRENT_DIRECTORY_PROPERTY, leftPanelLocalFileExplorer.getCurrentDirectory().toString());
                leftFileTable.setItems(leftPanelLocalFileExplorer.getFileList());
            }
        }
    }

    @FXML
    public void selectRightTableRow(MouseEvent mouseEvent) {
        FileInfo fileInfo = getFileInfo(rightFileTable);
        if (mouseEvent.getClickCount() == 2 && fileInfo.isDirectory()) {
            String currentDirectory = fileInfo.getFileName();
            logger.debug("selectRightTableRow() double click detected");
            if (isRightPanelRemote) {
                if (!connection.isAuthorized()) return;
                rightPanelRemoteFileExplorer.setCurrentDirectory(currentDirectory);
                rightFileTable.setItems(rightPanelRemoteFileExplorer.getFileList());
            } else {
                rightPanelLocalFileExplorer.setCurrentDirectory(currentDirectory);
                Settings.write(RIGHT_PANEL_LOCAL_CURRENT_DIRECTORY_PROPERTY, rightPanelLocalFileExplorer.getCurrentDirectory().toString());
                rightFileTable.setItems(rightPanelLocalFileExplorer.getFileList());
            }
        }
    }

    private FileInfo getFileInfo(TableView<FileInfo> fileTable) {
        TableView.TableViewSelectionModel<FileInfo> selectionModel = fileTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        ObservableList<FileInfo> selectedRow = selectionModel.getSelectedItems();
        return selectedRow.get(0);
    }

    public void swapLeftPanelToRemote(ActionEvent actionEvent) {
        if (!connection.isAuthorized()) return;
        if (isLeftPanelRemote)
            setLeftPanelShared();
        else if (isLeftPanelShared)
            setLeftPanelLocal();
        else
            setLeftPanelRemote();
    }

    private void setLeftPanelRemote() {
        isLeftPanelRemote = true;
        isLeftPanelShared = false;
        isLeftPanelLocal = false;
        leftFileTable.setItems(leftPanelRemoteFileExplorer.getFileList());
        swapLeftPanelButton.setText("Swap to shared files");
    }

    private void setLeftPanelLocal() {
        isLeftPanelLocal = true;
        isLeftPanelRemote = false;
        isLeftPanelShared = false;
        leftFileTable.setItems(leftPanelLocalFileExplorer.getFileList());
        swapLeftPanelButton.setText("Swap to remote storage");
    }

    private void setLeftPanelShared() {
        isLeftPanelShared = true;
        isLeftPanelLocal = false;
        isLeftPanelRemote = false;
        leftFileTable.setItems(sharedFileExplorer.getFileList());
        swapLeftPanelButton.setText("Swap to local storage");
    }

    public void swapRightPanelToRemote(ActionEvent actionEvent) {
        if (!connection.isAuthorized()) return;
        if (isRightPanelRemote)
            setRightPanelShared();
        else if (isRightPanelShared)
            setRightPanelLocal();
        else
            setRightPanelRemote();
    }

    private void setRightPanelShared() {
        isRightPanelRemote = false;
        isRightPanelLocal = false;
        isRightPanelShared = true;
        rightFileTable.setItems(sharedFileExplorer.getFileList());
        swapRightPanelButton.setText("Swap to local storage");
    }

    private void setRightPanelLocal() {
        isRightPanelRemote = false;
        isRightPanelLocal = true;
        isRightPanelShared = false;
        rightFileTable.setItems(rightPanelLocalFileExplorer.getFileList());
        swapRightPanelButton.setText("Swap to remote storage");
    }

    private void setRightPanelRemote() {
        isRightPanelRemote = true;
        isRightPanelLocal = false;
        isRightPanelShared = false;
        rightFileTable.setItems(rightPanelRemoteFileExplorer.getFileList());
        swapRightPanelButton.setText("Swap to shared files");
    }

    @Override
    public void connectionUpdate(CloudConnection connection) {
        this.connection = connection;
        logger.debug("MainWindowController. Connection status was changed. Authorized: " + connection.isAuthorized());
        if (connection.isAuthorized()) {
            registrationButton.setText("Disconnect");
            registrationButton.setOnAction((event) -> connectionHandler.disconnect());
            connectButton.setVisible(false);
        } else {
            registrationButton.setText("Registration");
            registrationButton.setOnAction(this::registration);
            connectButton.setVisible(true);
        }
    }

    public void copy(ActionEvent event) {
        if (isLeftPanelRemote && !isRightPanelRemote) {
            if (!connection.isAuthorized()) return;
            if (isLeftPanelActive && !isRightPanelActive)
                downloadFile(leftFileTable, rightFileTable, rightPanelLocalFileExplorer);
            if (isRightPanelActive && !isLeftPanelActive)
                upload(rightFileTable, leftFileTable, rightPanelLocalFileExplorer, leftPanelRemoteFileExplorer);
        }
        if (!isLeftPanelRemote && isRightPanelRemote) {
            if (!connection.isAuthorized()) return;
            if (isRightPanelActive && !isLeftPanelActive)
                downloadFile(rightFileTable, leftFileTable, leftPanelLocalFileExplorer);
            if (isLeftPanelActive && !isRightPanelActive)
                upload(leftFileTable, rightFileTable, leftPanelLocalFileExplorer, rightPanelRemoteFileExplorer);
        }
        if (isLeftPanelShared&&isLeftPanelActive&&isRightPanelLocal){
            if (!connection.isAuthorized()) return;
            downloadShareFile(leftFileTable, rightFileTable, rightPanelLocalFileExplorer);
        }
        if (isRightPanelShared&&isRightPanelActive&&isLeftPanelLocal){
            if (!connection.isAuthorized()) return;
            downloadShareFile(rightFileTable, leftFileTable, leftPanelLocalFileExplorer);
        }
        if (isLeftPanelLocal&&isRightPanelLocal&&isLeftPanelActive){
            localCopy(leftFileTable, rightFileTable, leftPanelLocalFileExplorer, rightPanelLocalFileExplorer);
        }
        if (isRightPanelLocal&&isLeftPanelLocal&&isRightPanelActive){
            localCopy(rightFileTable, leftFileTable, rightPanelLocalFileExplorer, leftPanelLocalFileExplorer);
        }
    }

    private void localCopy(TableView<FileInfo> fromTable, TableView<FileInfo> toTable, LocalFileExplorer fromLocalFileExplorer, LocalFileExplorer toLocalFileExplorer){
        FileInfo fileInfo = getFileInfo(fromTable);
        if (fileInfo == null) return;
        String fileName = fileInfo.getFileName();
        try {
            fromLocalFileExplorer.localCopy(fileName, toLocalFileExplorer.getCurrentDirectory());
        } catch (IOException e) {
            logger.error(e);
        }
        toTable.setItems(toLocalFileExplorer.getFileList());
    }

    private void downloadShareFile(TableView<FileInfo> fromTable, TableView<FileInfo> toTable, LocalFileExplorer localFileExplorer) {
        FileInfo fileInfo = getFileInfo(fromTable);
        if (fileInfo == null) return;
        String fileName = fileInfo.getFileName();
        try {
            sharedFileDownloader.download(fileName, localFileExplorer.getCurrentDirectory());
        } catch (IOException e) {
            networkProblemSignaler(e);
        } catch (BadResponseException e) {
            serverDidNotSendFileSignaler(e);
        }
        toTable.setItems(localFileExplorer.getFileList());
    }

    private void upload(TableView<FileInfo> fromTable, TableView<FileInfo> toTable, LocalFileExplorer localFileExplorer, RemoteFileExplorer remoteFileExplorer) {
        FileInfo fileInfo = getFileInfo(fromTable);
        if (fileInfo == null) return;
        String fileName = fileInfo.getFileName();
        try {
            fileUploader.upload(fileName, localFileExplorer.getCurrentDirectory());
        } catch (IOException e) {
            networkProblemSignaler(e);
        } catch (BadResponseException e) {
            serverDidNotReceiveFileSignaler(e);
        }
        remoteFileExplorer.setCurrentDirectory("");
        toTable.setItems(remoteFileExplorer.getFileList());
    }

    private void downloadFile(TableView<FileInfo> fromTable, TableView<FileInfo> toTable, LocalFileExplorer localFileExplorer) {
        FileInfo fileInfo = getFileInfo(fromTable);
        if (fileInfo == null) return;
        String fileName = fileInfo.getFileName();
        try {
            remoteFileDownloader.download(fileName, localFileExplorer.getCurrentDirectory());
        } catch (IOException e) {
            networkProblemSignaler(e);
        } catch (BadResponseException e) {
            serverDidNotSendFileSignaler(e);
        }
        toTable.setItems(localFileExplorer.getFileList());
    }

    private void serverDidNotSendFileSignaler(BadResponseException e) {
        ErrorWindowController controller = (ErrorWindowController) (windowCreator.createModalWindow("Error", ERROR_WINDOW).getController());
        controller.errorMessage.setText("Download incomplete. Server can't send file");
        controller.show();
        logger.error(e);
    }

    private void serverDidNotReceiveFileSignaler(BadResponseException e) {
        ErrorWindowController controller = (ErrorWindowController) (windowCreator.createModalWindow("Error", ERROR_WINDOW).getController());
        controller.errorMessage.setText("Upload incomplete. Server can't receive file");
        controller.show();
        logger.error(e);
    }

    private void networkProblemSignaler(IOException e) {
        ErrorWindowController controller = (ErrorWindowController) (windowCreator.createModalWindow("Error", ERROR_WINDOW).getController());
        controller.errorMessage.setText("Download incomplete. There was network problem. Try again later");
        controller.show();
        logger.error(e);
    }

    public void setLeftPanelActive(MouseEvent mouseEvent) {
        isLeftPanelActive = true;
        isRightPanelActive = false;
    }

    public void setRightPanelActive(MouseEvent mouseEvent) {
        isRightPanelActive = true;
        isLeftPanelActive = false;
    }

    public void shareFile(ActionEvent event) {
        FileInfo fileInfo = null;
        if (isLeftPanelActive && isLeftPanelRemote)
            fileInfo = getFileInfo(leftFileTable);
        if (isRightPanelActive && isRightPanelRemote)
            fileInfo = getFileInfo(rightFileTable);
        if (fileInfo == null) return;
        String fileName = fileInfo.getFileName();
        ShareWindowController controller = (ShareWindowController) (windowCreator.createModalWindow("Enter the username you are sharing the file with", SHARE_WINDOW).getController());
        controller.shareFileLabel.setText("File will shared: " + fileInfo.getFileName());
        controller.show();
        controller.shareButton.setOnAction(event1 -> {
            try {
                fileSharer.share(controller.usernameField.getText(), fileName);
            } catch (BadResponseException e) {
                shareProblemSignaler(e);
            }
            controller.close();
        });
    }

    private void shareProblemSignaler(BadResponseException e) {
        ErrorWindowController controller = (ErrorWindowController) (windowCreator.createModalWindow("Error", ERROR_WINDOW).getController());
        controller.errorMessage.setText("There was problem with file share. Try again later");
        controller.show();
        logger.error(e);
    }

    public void delete(ActionEvent event) {
        FileInfo fileInfo;
        if (isLeftPanelRemote&&isLeftPanelActive) {
            fileInfo = getFileInfo(leftFileTable);
            deleteRemoteFile(fileInfo);
            leftFileTable.setItems(leftPanelRemoteFileExplorer.getFileList());
        }
        if (isRightPanelRemote&&isRightPanelActive) {
            fileInfo = getFileInfo(rightFileTable);
            deleteRemoteFile(fileInfo);
            rightFileTable.setItems(rightPanelRemoteFileExplorer.getFileList());
        }
        if (isLeftPanelLocal&&isLeftPanelActive){
            deleteLocalFile(leftFileTable, leftPanelLocalFileExplorer);
        }
        if (isRightPanelLocal&&isRightPanelActive){
            deleteLocalFile(rightFileTable, rightPanelLocalFileExplorer);
        }
    }

    private void deleteRemoteFile(FileInfo fileInfo) {
        try {
            if (fileInfo == null) return;
            remoteFileRemover.delete(fileInfo.getFileName());
        } catch (BadResponseException e) {
            deleteProblemSignaler(e);
        }
    }

    private void deleteLocalFile(TableView<FileInfo> fromTable, LocalFileExplorer localFileExplorer){
        FileInfo fileInfo = getFileInfo(fromTable);
        if (fileInfo == null) return;
        String fileName = fileInfo.getFileName();
        try {
            localFileExplorer.localDelete(fileName);
        } catch (IOException e) {
            logger.error(e);
        }
        fromTable.setItems(localFileExplorer.getFileList());
    }

    private void deleteProblemSignaler(BadResponseException e) {
        ErrorWindowController controller = (ErrorWindowController) (windowCreator.createModalWindow("Error", ERROR_WINDOW).getController());
        controller.errorMessage.setText("There was problem with file share. Try again later");
        controller.show();
        logger.error(e);
    }
}
