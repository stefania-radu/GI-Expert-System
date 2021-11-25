package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    public void pressButton(ActionEvent event) throws IOException {
        Parent imagesPageParent = FXMLLoader.load(getClass().getResource("imageScene.fxml"));
        Scene imageScene = new Scene(imagesPageParent);

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(imageScene);
        appStage.show();

    }
}


