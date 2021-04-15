package ru.valerych.cloud2.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShareWindowController extends WindowController{

    private final Logger logger = LogManager.getLogger(ShareWindowController.class.getName());

    @FXML
    public VBox mainPane;
    @FXML
    public TextField usernameField;
    @FXML
    public Button shareButton;
    @FXML
    public Label shareFileLabel;

    @Override
    protected void show() {
        logger.debug("show() for share window");
        Stage primaryStage = (Stage) mainPane.getScene().getWindow();
        primaryStage.show();
    }

    @Override
    protected void close() {
        logger.debug("close() for share window");
        Stage primaryStage = (Stage) mainPane.getScene().getWindow();
        primaryStage.close();
    }
}
