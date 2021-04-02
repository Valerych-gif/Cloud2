package ru.valerych.cloud2.client.main;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Logger;

public class MainController {

    private final Logger logger = org.apache.logging.log4j.LogManager.getLogger(MainController.class.getName());

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

    public MainController() {

    }

    public void closeWindow(ActionEvent actionEvent) {
        logger.debug("closeWindow() " + actionEvent.getEventType().toString());
    }

    public void openSettings(ActionEvent actionEvent) {
        logger.debug("openSettings() " + actionEvent.getEventType().toString());
    }

    public void openAboutProgram(ActionEvent actionEvent) {
        logger.debug("openAboutProgram() " + actionEvent.getEventType().toString());
    }
}
