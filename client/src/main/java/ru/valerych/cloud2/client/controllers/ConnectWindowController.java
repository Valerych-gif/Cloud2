package ru.valerych.cloud2.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class ConnectWindowController {

    private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ConnectWindowController.class.getName());

    @FXML
    public TextField loginTextField;
    @FXML
    public TextField portTextField;
    @FXML
    public TextField passwordTextField;
    @FXML
    public TextField urlTextField;

    @FXML
    public void connect(ActionEvent actionEvent) {
        try {
            Socket socket = new Socket(urlTextField.getText(), Integer.parseInt(portTextField.getText()));
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @FXML
    public void registration(ActionEvent actionEvent) {
    }
}
