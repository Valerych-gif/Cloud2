package ru.valerych.cloud2.client.windows;

import javafx.stage.Stage;
import ru.valerych.cloud2.client.controllers.WindowController;

public class Cloud2Window {

    private Stage stage;
    private WindowController controller;

    public void show() {
        stage.show();
    }

    public void hide() {
        stage.close();
    }

    public WindowController getController() {
        return controller;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setController(WindowController controller) {
        this.controller = controller;
    }

    public Stage getStage() {
        return stage;
    }
}
