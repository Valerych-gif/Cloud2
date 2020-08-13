package modalWindows;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.mainController;

public class ShareModalWindow extends Application {

    private static mainController controller;

    public ShareModalWindow (mainController controller){
        ShareModalWindow.controller = controller;
    }

    public static mainController getController(){
        return controller;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../shareModalWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.setTitle("Share confirmation");
        primaryStage.showAndWait();
    }
}
