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
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.client.services.fileservices.LocalFileExplorer;
import ru.valerych.cloud2.client.services.fileservices.RemoteFileExplorer;
import ru.valerych.cloud2.client.windows.Cloud2Window;
import ru.valerych.cloud2.client.windows.WindowCreator;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends WindowController implements Initializable {

    private final Logger logger = LogManager.getLogger(MainWindowController.class.getName());
    private final String ABOUT_STAGE_TEMPLATE = "/stages/aboutWindow.fxml";
    private final String CONNECT_STAGE_TEMPLATE = "/stages/connectWindow.fxml";
    private final String REGISTRATION_STAGE_TEMPLATE = "/stages/registrationWindow.fxml";

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

    private ConnectWindowController connectWindowController;

    public MainWindowController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        windowCreator = new WindowCreator();
        setUpLeftPanel();
        setUpRightPanel();
    }

    private void setUpRightPanel() {
        rightPanelLocalFileExplorer = new LocalFileExplorer(rightPanel.getId());
        rightFileTable.setItems(rightPanelLocalFileExplorer.getFileList());
    }

    private void setUpLeftPanel() {
        leftPanelLocalFileExplorer = new LocalFileExplorer(leftPanel.getId());
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
    }

    public void registration(ActionEvent actionEvent) {
        logger.debug("registration() " + actionEvent.toString());
        windowCreator.createModalWindow("Registration", REGISTRATION_STAGE_TEMPLATE);
    }

    @Override
    public void close() {
        logger.debug("closeWindow() for main window");
        Stage primaryStage = (Stage)mainPane.getScene().getWindow();
        super.close(primaryStage);
    }

    @FXML
    public void selectLeftTableRow(MouseEvent mouseEvent) {
        FileInfo fileInfo = getFileInfo(leftFileTable);
        if (mouseEvent.getClickCount()==2&&fileInfo.isDirectory()){
            logger.debug("selectLeftTableRow() double click detected");
            if (isLeftPanelRemote){
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
        if (connectWindowController == null || connectWindowController.getConnection() == null) return;
        isLeftPanelRemote=!isLeftPanelRemote;
        if (isLeftPanelRemote) {
            this.connection = connectWindowController.getConnection();
            leftPanelRemoteFileExplorer = new RemoteFileExplorer(leftPanel.getId(), connection);
            leftFileTable.setItems(leftPanelRemoteFileExplorer.getFileList());
            swapLeftPanelButton.setText("Swap to local storage");
        } else {
            leftFileTable.setItems(leftPanelLocalFileExplorer.getFileList());
            swapLeftPanelButton.setText("Swap to remote storage");
        }
    }

    public void swapRightPanelToRemote(ActionEvent actionEvent) {
        if (connectWindowController == null || connectWindowController.getConnection() == null) return;
        isRightPanelRemote=!isRightPanelRemote;
        if (isRightPanelRemote) {
            this.connection = connectWindowController.getConnection();
            rightPanelRemoteFileExplorer = new RemoteFileExplorer(rightPanel.getId(), connection);
            rightFileTable.setItems(rightPanelRemoteFileExplorer.getFileList());
            swapRightPanelButton.setText("Swap to local storage");
        } else {
            rightFileTable.setItems(rightPanelRemoteFileExplorer.getFileList());
            swapRightPanelButton.setText("Swap to remote storage");
        }
    }
}
