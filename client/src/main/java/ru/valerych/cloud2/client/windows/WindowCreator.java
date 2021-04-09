package ru.valerych.cloud2.client.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class WindowCreator {

    private final static Logger logger = org.apache.logging.log4j.LogManager.getLogger(WindowCreator.class.getName());

    public Cloud2Window createSimpleWindow(String windowTitle, String pathToStagePattern){
        Cloud2Window controller = createWindow(windowTitle, pathToStagePattern, false);
        logger.debug(String.format("Simple window '%s' was created", windowTitle));
        return controller;
    }

    public Cloud2Window createModalWindow(String windowTitle, String pathToStagePattern){
        Cloud2Window controller = createWindow(windowTitle, pathToStagePattern, true);
        logger.debug(String.format("Modal window '%s' was created", windowTitle));
        return controller;
    }

    private Cloud2Window createWindow(String windowTitle, String pathToStagePattern, boolean isModal) {
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
            stage.setOnCloseRequest(event -> stage.close());
            Cloud2Window cloud2Window = new Cloud2Window();
            cloud2Window.setStage(stage);
            cloud2Window.setController(loader.getController());
            return cloud2Window;
        } catch (IOException e) {
            logger.error(e);
            stage.close();
        }
        return null;
    }
}
