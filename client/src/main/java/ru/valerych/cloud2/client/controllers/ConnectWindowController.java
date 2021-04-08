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
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.client.network.ConnectionHandler;
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

    private ConnectionHandler connectionHandler;

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

    public void login(ActionEvent event) {
        connectionHandler = new ConnectionHandler();
        try {
            connectionHandler.connectToServer(urlTextField.getText(), portTextField.getText());
            connectionHandler.loginToServer(loginTextField.getText(), passwordTextField.getText());
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
        } catch (LoginUnsuccessfulException | BadResponseException e) {
            logger.error(String.format("Log in to server %s:%s for user %s failed. Cause: %s",
                    urlTextField.getText(),
                    portTextField.getText(),
                    loginTextField.getText(),
                    e));
            errorLabel.setText("Login or password isn't correct");
            connectionHandler.disconnect();
        }
    }

    public CloudConnection getConnection(){
        return connectionHandler.getConnection();
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
