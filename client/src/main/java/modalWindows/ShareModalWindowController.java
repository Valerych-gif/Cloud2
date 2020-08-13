package modalWindows;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ShareModalWindowController implements Initializable {

    private main.mainController mainController;
    private Stage stage;

    @FXML
    public TextField nickNameField;

    @FXML
    public Button shareConfirmButton;

    @FXML
    public Button cancelButton;

    public ShareModalWindowController(){
        this.mainController = ShareModalWindow.getController();

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        shareConfirmButton.setOnAction(a->{
            String nickName = nickNameField.getText();
            mainController.shareFileByNickname(nickName);
            stage = (Stage) shareConfirmButton.getScene().getWindow();
            stage.close();
        });

        cancelButton.setOnAction(a-> {
            stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });

    }
}
