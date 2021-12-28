package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class EndSceneController {

    @FXML
    Button endButton;

    public void pressButton(ActionEvent event) {
        System.exit(0);
    }
}
