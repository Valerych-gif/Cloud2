package ru.valerych.cloud2.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.utils.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConnectWindowController extends WindowController implements Initializable {

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
    public VBox window;
    @FXML
    public Label errorLabel;
    @FXML
    public CheckBox saveData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginTextField.setText(Settings.read("login"));
        passwordTextField.setText(Settings.read("password"));
        urlTextField.setText(Settings.read("host"));
        portTextField.setText(Settings.read("port"));
    }

    @Override
    public void close() {
        logger.debug("closeWindow() for login window");
        Stage primaryStage = (Stage)window.getScene().getWindow();
        super.close(primaryStage);
    }
}
