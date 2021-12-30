package ui;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class EndSceneController {

    public void pressButton(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.close();
    }
}
