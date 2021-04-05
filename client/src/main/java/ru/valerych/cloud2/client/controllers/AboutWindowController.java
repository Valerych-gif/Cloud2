package ru.valerych.cloud2.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutWindowController extends WindowController implements Initializable {

    private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(AboutWindowController.class.getName());

    @FXML
    public TextArea aboutTextArea;

    public AboutWindowController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aboutTextArea.setEditable(false);
        aboutTextArea.setText("lll");
    }

    @Override
    protected void closeWindow() {

    }
}
