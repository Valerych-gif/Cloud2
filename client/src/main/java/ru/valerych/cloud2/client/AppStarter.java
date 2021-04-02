package ru.valerych.cloud2.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppStarter extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cloud2.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Cloud2");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> Platform.exit());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
