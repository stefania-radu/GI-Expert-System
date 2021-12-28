package ui;

import helper.FileWriterHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.AnswerValidationType;
import model.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ui.StartController.questionList;

public class QuestionSceneController {

    @FXML
    Label question;
    @FXML
    VBox responsePanel;
    @FXML
    Label errorMessage;

    public static List<Label> variablesLabels = new ArrayList<>();

    private static int questionNumber = 1;

    @FXML
    public void initialize() {

        responsePanel.setSpacing(10);
        Question currentQuestion = Question.getQuestionById(questionList, questionNumber);
        question.setText(currentQuestion.getName());

        for (Map.Entry<Integer, String> currentAnswer : currentQuestion.getPossibleAnswers().entrySet()) {

            RadioButton answer = new RadioButton(currentAnswer.getValue());
            answer.setId(currentAnswer.getKey().toString());
            answer.setFont(Font.font ("arial", 18));
            responsePanel.getChildren().add(answer);
        }
        if(questionNumber == 1) {
            initLabels();
        }
    }

    @FXML
    public void changeQuestion(ActionEvent event) throws IOException {

        List<Node> children = responsePanel.getChildren();
        Map<Integer, Boolean> givenAnswers = new HashMap<>();

        for (Node child : children) {
            RadioButton answerGiven = (RadioButton) child;
            givenAnswers.put(Integer.parseInt(answerGiven.getId()), answerGiven.isSelected());
        }

        if (isValid(givenAnswers, AnswerValidationType.SINGLE_ANSWER)) {
            Question currentQuestion = Question.getQuestionById(questionList, questionNumber);
            currentQuestion.setGivenAnswers(givenAnswers);
            currentQuestion.setVariableValue(String.valueOf(givenAnswers.get(1)));

            updateLabels();

            System.out.println(currentQuestion);

            questionNumber = currentQuestion.evaluatePoints();
            if (questionNumber == 0) {
                showEndScene(event);
                FileWriterHelper.cleanDomainFile();
            } else {
                responsePanel.getChildren().clear();
                initialize();
                errorMessage.setText("");
            }
        }
    }

    private boolean isValid(Map<Integer, Boolean> givenAnswers, AnswerValidationType validationType) {
        int nrCheckedBoxes = 0;
        boolean isValid = true;
        for (Map.Entry<Integer, Boolean> entry : givenAnswers.entrySet()) {
            if (entry.getValue()) {
                nrCheckedBoxes++;
            }
        }
        if (nrCheckedBoxes > 1) {
            errorMessage.setText("Please select only one answer!");
            isValid = false;
        } else if (nrCheckedBoxes == 0) {
            errorMessage.setText("Please select an option!");
            isValid = false;
        }
        return isValid;
    }

    @FXML
    public void pressDomainButton(ActionEvent event) throws IOException {
        Stage domainStage = new Stage();

        domainStage.initModality(Modality.APPLICATION_MODAL);
        domainStage.setTitle("Domain Variables");

        Parent domainParent = FXMLLoader.load(getClass().getResource("domainScene.fxml"));
        Scene domainScene = new Scene(domainParent);

        domainStage.setScene(domainScene);

        domainStage.showAndWait();

    }

    private void initLabels() {
        for(Question q : questionList) {
            Label domainEntryLabel = new Label(q.getVariableName() + ": " + q.getVariableValue());
            domainEntryLabel.setFont(new Font("Verdana", 16));
            variablesLabels.add(domainEntryLabel);
        }
    }

    private void updateLabels() {
        for(int i = 0; i < questionList.size(); i++) {
            Question currentQuestion = questionList.get(i);
            String newText = currentQuestion.getVariableName() + ": " + currentQuestion.getVariableValue();
            variablesLabels.get(i).setText(newText);
        }
    }


    private void showEndScene(ActionEvent event) throws IOException {
        Parent endSceneParent = FXMLLoader.load(getClass().getResource("endScene.fxml"));
        Scene endScene = new Scene(endSceneParent);

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(endScene);
        appStage.show();

    }

}


