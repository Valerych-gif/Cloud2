package ru.valerych.cloud2.client.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.entities.FileInfo;
import ru.valerych.cloud2.client.network.CloudConnection;
import ru.valerych.cloud2.client.network.ConnectionHandler;
import ru.valerych.cloud2.client.network.RegistrationHandler;
import ru.valerych.cloud2.client.windows.Cloud2Window;
import ru.valerych.cloud2.client.windows.WindowCreator;

public class MainWindowController extends WindowController {

    private final Logger logger = LogManager.getLogger(MainWindowController.class.getName());
    private final String ABOUT_STAGE_TEMPLATE = "/stages/aboutWindow.fxml";
    private final String CONNECT_STAGE_TEMPLATE = "/stages/connectWindow.fxml";
    private final String REGISTRATION_STAGE_TEMPLATE = "/stages/registrationWindow.fxml";

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
    public Button connectButton;
    @FXML
    public Button registrationButton;
    @FXML
    public TableColumn<FileInfo, String> nameColumnHeaderLeft;
    @FXML
    public TableColumn<FileInfo, String> nameColumnHeaderRight;
    @FXML
    public TextField addrTextFieldLeft;
    @FXML
    public TextField addrTextFieldRight;

    private CloudConnection connection;
    private final WindowCreator windowCreator;

    public MainWindowController() {
        windowCreator = new WindowCreator();
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
        ConnectionHandler connectionHandler = new ConnectionHandler();
        connection = connectionHandler.init((ConnectWindowController) window.getController());
    }

    public void registration(ActionEvent actionEvent) {
        logger.debug("registration() " + actionEvent.toString());
        Cloud2Window window = windowCreator.createModalWindow("Registration", REGISTRATION_STAGE_TEMPLATE);
        RegistrationHandler registrationHandler = new RegistrationHandler();
        connection = registrationHandler.init((RegistrationWindowController) window.getController());
    }

    @Override
    public void close() {
        logger.debug("closeWindow() for main window");
        Stage primaryStage = (Stage)mainPane.getScene().getWindow();
        super.close(primaryStage);
    }
}
