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
import ru.valerych.cloud2.client.services.fileservices.FileDownloader;
import ru.valerych.cloud2.client.services.fileservices.LocalFileExplorer;
import ru.valerych.cloud2.client.services.fileservices.RemoteFileExplorer;
import ru.valerych.cloud2.client.windows.Cloud2Window;
import ru.valerych.cloud2.client.windows.WindowCreator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends WindowController implements Initializable, ConnectionObserver {
    private final Logger logger = LogManager.getLogger(MainWindowController.class.getName());

    private static final String ERROR_WINDOW = "/modalWindows/errorDownloadProcess.fxml";
    private static final String ABOUT_STAGE_TEMPLATE = "/modalWindows/aboutWindow.fxml";
    private static final String CONNECT_STAGE_TEMPLATE = "/modalWindows/connectWindow.fxml";
    private static final String REGISTRATION_STAGE_TEMPLATE = "/modalWindows/registrationWindow.fxml";

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
    public TableView<FileInfo> leftFileTable;
    public TableView<FileInfo> rightFileTable;

    private CloudConnection connection;
    private WindowCreator windowCreator;
    private LocalFileExplorer leftPanelLocalFileExplorer;
    private LocalFileExplorer rightPanelLocalFileExplorer;
    private RemoteFileExplorer leftPanelRemoteFileExplorer;
    private RemoteFileExplorer rightPanelRemoteFileExplorer;
    private boolean isLeftPanelRemote;
    private boolean isRightPanelRemote;

    private final FileDownloader fileDownloader;

    private ConnectWindowController connectWindowController;
    private final ConnectionHandler connectionHandler;

    public MainWindowController() {
        connectionHandler = new ConnectionHandler();
        connection = new CloudConnection();
        connection.setAuthorized(false);

        fileDownloader = new FileDownloader();

        connectionHandler.registerObserver(this);
        connectionHandler.registerObserver(fileDownloader);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        windowCreator = new WindowCreator();

        setUpLeftPanel();
        setUpRightPanel();
    }

    private void setUpRightPanel() {
        rightPanelLocalFileExplorer = new LocalFileExplorer(rightPanel.getId());
        rightPanelRemoteFileExplorer = new RemoteFileExplorer(rightPanel.getId());
        connectionHandler.registerObserver(rightPanelRemoteFileExplorer);
        rightFileTable.setItems(rightPanelLocalFileExplorer.getFileList());
    }

    private void setUpLeftPanel() {
        leftPanelLocalFileExplorer = new LocalFileExplorer(leftPanel.getId());
        leftPanelRemoteFileExplorer = new RemoteFileExplorer(leftPanel.getId());
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
        connectWindowController = (ConnectWindowController) window.getController();
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
        Stage primaryStage = (Stage)mainPane.getScene().getWindow();
        primaryStage.show();
    }

    @Override
    public void close() {
        logger.debug("close() for main window");
        Stage primaryStage = (Stage)mainPane.getScene().getWindow();
        primaryStage.close();
    }

    @FXML
    public void selectLeftTableRow(MouseEvent mouseEvent) {
        FileInfo fileInfo = getFileInfo(leftFileTable);
        if (mouseEvent.getClickCount()==2&&fileInfo.isDirectory()){
            logger.debug("selectLeftTableRow() double click detected");
            if (isLeftPanelRemote){
                if (!connection.isAuthorized()) return;
                leftPanelRemoteFileExplorer.setCurrentDirectory(fileInfo.getFileName());
                leftFileTable.setItems(leftPanelRemoteFileExplorer.getFileList());
            } else {
                leftPanelLocalFileExplorer.setCurrentDirectory(fileInfo.getPath());
                leftFileTable.setItems(leftPanelLocalFileExplorer.getFileList());
            }
        }
    }

    @FXML
    public void selectRightTableRow(MouseEvent mouseEvent) {
        FileInfo fileInfo = getFileInfo(rightFileTable);
        if (mouseEvent.getClickCount()==2&&fileInfo.isDirectory()){
            logger.debug("selectRightTableRow() double click detected");
            if (isRightPanelRemote){
                if (!connection.isAuthorized()) return;
                rightPanelRemoteFileExplorer.setCurrentDirectory(fileInfo.getFileName());
                rightFileTable.setItems(rightPanelRemoteFileExplorer.getFileList());
            } else {
                rightPanelLocalFileExplorer.setCurrentDirectory(fileInfo.getPath());
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
        isLeftPanelRemote=!isLeftPanelRemote;
        if (isLeftPanelRemote) {
            this.connection = connectWindowController.getConnection();
            leftFileTable.setItems(leftPanelRemoteFileExplorer.getFileList());
            swapLeftPanelButton.setText("Swap to local storage");
        } else {
            leftFileTable.setItems(leftPanelLocalFileExplorer.getFileList());
            swapLeftPanelButton.setText("Swap to remote storage");
        }
    }

    public void swapRightPanelToRemote(ActionEvent actionEvent) {
        if (!connection.isAuthorized()) return;
        isRightPanelRemote=!isRightPanelRemote;
        if (isRightPanelRemote) {
            this.connection = connectWindowController.getConnection();
            rightFileTable.setItems(rightPanelRemoteFileExplorer.getFileList());
            swapRightPanelButton.setText("Swap to local storage");
        } else {
            rightFileTable.setItems(rightPanelLocalFileExplorer.getFileList());
            swapRightPanelButton.setText("Swap to remote storage");
        }
    }

    @Override
    public void connectionUpdate(CloudConnection connection) {
        this.connection = connection;
        logger.debug("MainWindowController. Connection status was changed. Authorized: " + connection.isAuthorized());
        if (connection.isAuthorized()) {
            registrationButton.setText("Disconnect");
            registrationButton.setOnAction((event)->connectionHandler.disconnect());
            connectButton.setVisible(false);
        }
        else {
            registrationButton.setText("Registration");
            registrationButton.setOnAction(this::registration);
            connectButton.setVisible(true);
        }
    }

    public void copy(ActionEvent event) {
        if (isLeftPanelRemote&&!isRightPanelRemote){
            if (!connection.isAuthorized()) return;
            FileInfo fileInfo = getFileInfo (leftFileTable);
            if (fileInfo == null) return;
            String fileName = fileInfo.getFileName();
            try {
                fileDownloader.download(fileName, rightPanelLocalFileExplorer.getCurrentDirectory());
            } catch (IOException e) {
                networkProblemSignaler(e);
            } catch (BadResponseException e){
                setverDidNotSendFileSignaler(e);
            }
            rightFileTable.setItems(rightPanelLocalFileExplorer.getFileList());
        }
        if (!isLeftPanelRemote&&isRightPanelRemote){
            if (!connection.isAuthorized()) return;
            FileInfo fileInfo = getFileInfo (rightFileTable);
            if (fileInfo == null) return;
            String fileName = fileInfo.getFileName();
            try {
                fileDownloader.download(fileName, leftPanelLocalFileExplorer.getCurrentDirectory());
            } catch (IOException e) {
                networkProblemSignaler(e);
            } catch (BadResponseException e){
                setverDidNotSendFileSignaler(e);
            }
            leftFileTable.setItems(leftPanelLocalFileExplorer.getFileList());
        }
    }

    private void setverDidNotSendFileSignaler(BadResponseException e) {
        ErrorWindowController controller = (ErrorWindowController) (windowCreator.createModalWindow("Error", ERROR_WINDOW).getController());
        controller.errorMessage.setText("Download incomplete. Server can't send file");
        controller.show();
        logger.error(e);
    }

    private void networkProblemSignaler(IOException e) {
        ErrorWindowController controller = (ErrorWindowController) (windowCreator.createModalWindow("Error", ERROR_WINDOW).getController());
        controller.errorMessage.setText("Download incomplete. There was network problem. Try again later");
        controller.show();
        logger.error(e);
    }
}
