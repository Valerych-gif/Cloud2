package main;

import commands.Requests;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;

public class Main extends Application {

    public static final boolean DEBUG_MODE = true;

    public final static String CLIENT_DIR_PATH = "client/src/main/resources/Cloud2Files";
    public final static String SERVER = "localhost";
    public final static int PORT = 8189;

    public final static int BUFFER_SIZE = 1024;
    public final static char END_COMMAND_CHAR = '|';

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Cloud2.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        DataOutputStream os = controller.getOs();
        primaryStage.setTitle("Cloud2");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            try {
                os.writeByte(Requests.CLOSE_CONNECTION.get());
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
