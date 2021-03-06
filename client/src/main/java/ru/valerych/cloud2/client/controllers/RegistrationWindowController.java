package ru.valerych.cloud2.client.controllers;

import javafx.event.ActionEvent;
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
import ru.valerych.cloud2.client.exceptions.BadResponseException;
import ru.valerych.cloud2.client.exceptions.LoginUnsuccessfulException;
import ru.valerych.cloud2.client.network.ConnectionHandler;
import ru.valerych.cloud2.client.utils.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationWindowController extends WindowController implements Initializable {

    private final Logger logger = LogManager.getLogger(RegistrationWindowController.class.getName());

    @FXML
    public TextField loginTextField;
    @FXML
    public TextField portTextField;
    @FXML
    public TextField passwordTextField;
    @FXML
    public TextField urlTextField;
    @FXML
    public Button registrationButton;
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

    @FXML
    public void registration(ActionEvent event) {
        ConnectionHandler connectionHandler = new ConnectionHandler();
        try {
            connectionHandler.connectToServer(urlTextField.getText(), portTextField.getText());
            connectionHandler.registrationToServer(loginTextField.getText(), passwordTextField.getText());
            close();
            if (saveData.isSelected()) {
                writeConnectionSettings(
                        urlTextField.getText(),
                        portTextField.getText(),
                        loginTextField.getText(),
                        passwordTextField.getText()
                );
            } else {
                clearConnectionSettings();
            }
        } catch (IOException e) {
            logger.error(String.format("Connection to server %s:%s failed. Cause: %s",
                    urlTextField.getText(),
                    portTextField.getText(),
                    e));
            errorLabel.setText("There is trouble with connection. Please try again later.");
            connectionHandler.disconnect();
        } catch (BadResponseException e) {
            logger.error(String.format("Registration on server %s:%s for user %s failed. Cause: %s",
                    urlTextField.getText(),
                    portTextField.getText(),
                    loginTextField.getText(),
                    e));
            errorLabel.setText("Login or password isn't correct");
            connectionHandler.disconnect();
        }
    }

    @Override
    protected void show() {
        logger.debug("show() for registration window");
        Stage primaryStage = (Stage) window.getScene().getWindow();
        primaryStage.show();
    }

    @Override
    public void close() {
        logger.debug("close() for registration window");
        Stage primaryStage = (Stage) window.getScene().getWindow();
        primaryStage.close();
    }

    private void writeConnectionSettings(String host, String port, String login, String password) {
        Settings.write("host", host);
        Settings.write("port", port);
        Settings.write("login", login);
        Settings.write("password", password);
    }

    private void clearConnectionSettings() {
        try {
            Settings.delete("host");
            Settings.delete("port");
            Settings.delete("login");
            Settings.delete("password");
        } catch (IOException e) {
            logger.error(String.format("Can't delete settings from settings file. Cause: %s", e));
        }
    }
}
