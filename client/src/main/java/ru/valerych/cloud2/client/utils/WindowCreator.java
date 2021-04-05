package ru.valerych.cloud2.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import ru.valerych.cloud2.client.controllers.WindowController;

import java.io.IOException;

public class WindowCreator {

    private final static Logger logger = org.apache.logging.log4j.LogManager.getLogger(WindowCreator.class.getName());

    public static WindowController createSimpleWindow(String windowTitle, String pathToStagePattern){
        WindowController controller = createWindow(windowTitle, pathToStagePattern, false);
        logger.debug(String.format("Simple window '%s' was created", windowTitle));
        return controller;
    }

    public static WindowController createModalWindow(String windowTitle, String pathToStagePattern){
        WindowController controller = createWindow(windowTitle, pathToStagePattern, true);
        logger.debug(String.format("Modal window '%s' was created", windowTitle));
        return controller;
    }

    private static WindowController createWindow(String windowTitle, String pathToStagePattern, boolean isModal) {
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
            return loader.getController();
        } catch (IOException e) {
            logger.error(e);
            stage.close();
        }
        return null;
    }
}
