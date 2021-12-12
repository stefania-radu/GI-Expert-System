package ui;

import helper.QuestionsLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.AnswerValidationType;
import model.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionSceneController {

    @FXML
    Label question;
    @FXML
    VBox responsePanel;
    @FXML
    Label errorMessage;

    private static int questionNumber = 1;
    private static List<Question> questionList = QuestionsLoader.load();

    @FXML
    public void initialize() {

        responsePanel.setSpacing(10);
        Question currentQuestion = Question.getQuestionById(questionList, questionNumber);
        question.setText(currentQuestion.getName());

        for (Map.Entry<Integer, String> currentAnswer : currentQuestion.getPossibleAnswers().entrySet()) {

            CheckBox answer = new CheckBox(currentAnswer.getValue());
            answer.setId(currentAnswer.getKey().toString());
            responsePanel.getChildren().add(answer);
        }

    }

    @FXML
    public void changeQuestion(ActionEvent event) {

        List<Node> children = responsePanel.getChildren();
        Map<Integer, Boolean> givenAnswers = new HashMap<>();

        for (Node child : children) {
            CheckBox answerGiven = (CheckBox) child;
            givenAnswers.put(Integer.parseInt(answerGiven.getId()), answerGiven.isSelected());

        }

        if (isValid(givenAnswers, AnswerValidationType.SINGLE_ANSWER)) {
            Question currentQuestion = Question.getQuestionById(questionList, questionNumber);
            currentQuestion.setGivenAnswers(givenAnswers);

            System.out.println(currentQuestion.toString());

            // find next question()
            questionNumber++;
            responsePanel.getChildren().clear();

            initialize();
            errorMessage.setText("");
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
}
