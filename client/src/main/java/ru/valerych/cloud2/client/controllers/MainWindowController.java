package ru.valerych.cloud2.client.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.entities.FileInfo;

import static ru.valerych.cloud2.client.utils.WindowCreator.createModalWindow;
import static ru.valerych.cloud2.client.utils.WindowCreator.createSimpleWindow;

public class MainWindowController {

    private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(MainWindowController.class.getName());
    private final String ABOUT_STAGE_TEMPLATE = "/stages/aboutWindow.fxml";
    private final String CONNECT_STAGE_TEMPLATE = "/stages/connectWindow.fxml";

    @FXML
    public Button refreshButton;

    @FXML
    public Button copyButton;

    @FXML
    public Button moveButton;

    @FXML
    public Button deleteButton;

    @FXML
    public Button shareButton;

    @FXML
    public VBox mainPane;

    @FXML
    public MenuItem closeMenuItem;

    @FXML
    public MenuItem settingsMenuItem;

    @FXML
    public MenuItem aboutMenuItem;

    @FXML
    public Button cutButton;

    @FXML
    public Button pasteButton;

    @FXML
    public TableColumn<FileInfo, String> nameColumnHeaderLeft;

    @FXML
    public TableColumn<FileInfo, String> nameColumnHeaderRight;

    @FXML
    public TextField addrTextFieldLeft;

    @FXML
    public TextField addrTextFieldRight;

    public MainWindowController() {

    }

    public void closeWindow(ActionEvent actionEvent) {
        logger.debug("closeWindow() " + actionEvent.toString());
        Stage primaryStage = (Stage)mainPane.getScene().getWindow();
        primaryStage.close();
    }

    public void openSettings(ActionEvent actionEvent) {
        logger.debug("openSettings() " + actionEvent.toString());
    }

    public void openAboutProgram(ActionEvent actionEvent) {
        logger.debug("openAboutProgram() " + actionEvent.toString());
        createModalWindow("About", ABOUT_STAGE_TEMPLATE);
    }

    public void connectToServer(ActionEvent actionEvent) {
        logger.debug("connectToServer() " + actionEvent.toString());
        createModalWindow("Connect", CONNECT_STAGE_TEMPLATE);
    }
}
