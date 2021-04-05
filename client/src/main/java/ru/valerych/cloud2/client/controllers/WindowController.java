package ru.valerych.cloud2.client.controllers;

import javafx.stage.Stage;

public abstract class WindowController {

    protected void closeWindow(Stage stage){
        stage.close();
    }

    protected abstract void closeWindow();
}
