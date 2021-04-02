package ru.valerych.cloud2.client.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class WindowCreator {

    private final static Logger logger = org.apache.logging.log4j.LogManager.getLogger(WindowCreator.class.getName());

    public static void createSimpleWindow(String windowTitle, String pathToStagePattern){
        FXMLLoader loader = new FXMLLoader(WindowCreator.class.getResource(pathToStagePattern));
        Parent root;
        Stage stage = new Stage();
        try {
            root = loader.load();
            stage.setTitle(windowTitle);
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnCloseRequest(event -> stage.close());
        } catch (IOException e) {
            logger.error(e);
            stage.close();
        }
    }
}
