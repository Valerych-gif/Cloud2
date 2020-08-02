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

    public final static String CLIENT_DIR_PATH = "./client/src/main/resources/";
    public final static int PORT = 8189;

    public final static int BUFFER_SIZE = 1024;
    public final static char END_COMMAND_CHAR = '|';

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
                os.writeBytes(Commands.CLOSE_CONNECTION.getString() + END_COMMAND_CHAR);
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
