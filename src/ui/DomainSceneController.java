package ui;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import static ui.QuestionSceneController.variablesLabels;

public class DomainSceneController {

    @FXML
    VBox vBox;

    @FXML
    public void initialize() {
        vBox.setSpacing(7);
        vBox.getChildren().addAll(variablesLabels);
    }

}
