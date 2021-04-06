package ru.valerych.cloud2.client.controllers;

import javafx.stage.Stage;

public abstract class WindowController {

    protected void close(Stage stage){
        stage.close();
    }

    protected abstract void close();
}
