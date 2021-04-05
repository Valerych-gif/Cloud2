package ru.valerych.cloud2.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectWindowController extends WindowController {

    private final Logger logger = LogManager.getLogger(ConnectWindowController.class.getName());

    @FXML
    public TextField loginTextField;
    @FXML
    public TextField portTextField;
    @FXML
    public TextField passwordTextField;
    @FXML
    public TextField urlTextField;
    @FXML
    public Button loginButton;
    @FXML
    public VBox loginWindow;
    @FXML
    public Label errorLabel;

    @Override
    public void closeWindow() {
        logger.debug("closeWindow() for login window");
        Stage primaryStage = (Stage)loginWindow.getScene().getWindow();
        super.closeWindow(primaryStage);
    }
}
