import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataOutputStream;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        DataOutputStream os = controller.getOs();
        primaryStage.setTitle("Cloud2");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            try {
                os.writeUTF("./closeconnection");
            } catch (IOException e) {
                e.printStackTrace();
            };
            Platform.exit();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
