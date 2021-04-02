package ru.valerych.cloud2.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class WindowCreator {

    private final static Logger logger = org.apache.logging.log4j.LogManager.getLogger(WindowCreator.class.getName());

    public static void createSimpleWindow(String windowTitle, String pathToStagePattern){
        createWindow(windowTitle, pathToStagePattern, false);
        logger.debug(String.format("Simple window '%s' was created", windowTitle));
    }

    public static void createModalWindow(String windowTitle, String pathToStagePattern){
        createWindow(windowTitle, pathToStagePattern, true);
        logger.debug(String.format("Modal window '%s' was created", windowTitle));
    }

    private static void createWindow(String windowTitle, String pathToStagePattern, boolean isModal) {
        FXMLLoader loader = new FXMLLoader(WindowCreator.class.getResource(pathToStagePattern));
        Parent root;
        Stage stage = new Stage();
        try {
            root = loader.load();
            stage.setTitle(windowTitle);
            if (isModal) {
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
            }
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnCloseRequest(event -> stage.close());
        } catch (IOException e) {
            logger.error(e);
            stage.close();
        }
    }
}
