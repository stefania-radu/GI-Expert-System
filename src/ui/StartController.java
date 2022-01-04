package ui;

import helper.QuestionsLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Question;

import java.io.IOException;
import java.util.List;

public class StartController {

    public static List<Question> questionList = QuestionsLoader.load();

    @FXML
    public void pressButton(ActionEvent event) throws IOException {

        Parent questionPageParent = FXMLLoader.load(getClass().getResource("questionScene.fxml"));

        Scene questionScene = new Scene(questionPageParent);

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setResizable(false);

        appStage.setScene(questionScene);

        appStage.show();

    }
}


