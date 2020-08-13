package modalWindows;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ShareModalWindowController implements Initializable {

    private main.mainController mainController;

    @FXML
    public TextField nickNameField;

    @FXML
    public Button shareConfirmButton;

    public ShareModalWindowController(){
        this.mainController = ShareModalWindow.getController();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        shareConfirmButton.setOnAction(a->{
            String nickName = nickNameField.getText();
            mainController.shareFileByNickname(nickName);
        });
    }
}
