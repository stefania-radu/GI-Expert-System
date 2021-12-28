package ui;

import helper.FileWriterHelper;
import helper.QuestionsLoader;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.AnswerValidationType;
import model.Question;

import java.io.IOException;
import java.util.*;

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
        ToggleGroup answersGroup = new ToggleGroup();

        for (Map.Entry<Integer, String> currentAnswer : currentQuestion.getPossibleAnswers().entrySet()) {

            if (currentQuestion.getAnswerType() == AnswerValidationType.SINGLE_ANSWER) {

                RadioButton answer = new RadioButton(currentAnswer.getValue());
                answer.setId(currentAnswer.getKey().toString());
                answer.setFont(Font.font ("arial", 18));
                answer.setToggleGroup(answersGroup);
                responsePanel.getChildren().add(answer);
            } else if (currentQuestion.getAnswerType() == AnswerValidationType.MULTIPLE_ANSWER) {

                CheckBox answer = new CheckBox(currentAnswer.getValue());
                answer.setId(currentAnswer.getKey().toString());
                answer.setFont(Font.font ("arial", 18));
                answer.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                    List<Node> answers = responsePanel.getChildren();

                    for (Node answerNode : answers) {
                        CheckBox answer1 = (CheckBox) answerNode;
                        if (Objects.equals(answer1.getId(), "-1")) {
                            answer1.setSelected(false);
                        }
                    }
                });
                responsePanel.getChildren().add(answer);
            }
        }

        addNoneAnswer(currentQuestion);

        if(questionNumber == 1) {
            initLabels();
        }
    }

    private void addNoneAnswer(Question currentQuestion) {
        if (currentQuestion.getAnswerType() == AnswerValidationType.MULTIPLE_ANSWER) {
            CheckBox noneAnswer = new CheckBox("None of the above");
            noneAnswer.setFont(Font.font ("arial", 18));
            noneAnswer.setId("-1");
            noneAnswer.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                List<Node> answers = responsePanel.getChildren();

                for (Node answerNode : answers) {
                    CheckBox answer = (CheckBox) answerNode;
                    if (!Objects.equals(answer.getId(), "-1")) {
                        answer.setSelected(false);
                    }
                }
            });
            responsePanel.getChildren().add(noneAnswer);


        }
    }


    @FXML
    public void changeQuestion(ActionEvent event) throws IOException {
        Question currentQuestion = Question.getQuestionById(questionList, questionNumber);

        List<Node> children = responsePanel.getChildren();
        Map<Integer, Boolean> givenAnswers = new HashMap<>();

        for (Node child : children) {
            if (currentQuestion.getAnswerType() == AnswerValidationType.SINGLE_ANSWER) {
                RadioButton answerGiven = (RadioButton) child;
                givenAnswers.put(Integer.parseInt(answerGiven.getId()), answerGiven.isSelected());
            } else if (currentQuestion.getAnswerType() == AnswerValidationType.MULTIPLE_ANSWER) {
                CheckBox answerGiven = (CheckBox) child;
                givenAnswers.put(Integer.parseInt(answerGiven.getId()), answerGiven.isSelected());
            }
        }


        if (isValid(givenAnswers, currentQuestion.getAnswerType())) {
            currentQuestion.setGivenAnswers(givenAnswers);
            int i = 1;
            for (Map.Entry<String, String> domainEntry : currentQuestion.getDomainEntry().entrySet()) {
                if (!(currentQuestion.getAnswerType() == AnswerValidationType.MULTIPLE_ANSWER && i == givenAnswers.size())) {
                    domainEntry.setValue(String.valueOf(givenAnswers.get(i)));
                    i++;
                }

            }

            updateLabels();

            System.out.println(currentQuestion);

            findNextQuestion(currentQuestion, event);
        }
    }

    private void findNextQuestion(Question currentQuestion, ActionEvent event) throws IOException {
        questionNumber = currentQuestion.evaluatePoints();
        if (questionNumber == 0) {
            showEndScene(event);
//            FileWriterHelper.cleanDomainFile();
        } else {
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
        if (nrCheckedBoxes == 0) {
            errorMessage.setText("Please select an option!");
            isValid = false;
        }

        if (validationType == AnswerValidationType.MULTIPLE_ANSWER && nrCheckedBoxes == 1 && givenAnswers.get(-1)) {

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
            for (Map.Entry<String, String> domainEntry : q.getDomainEntry().entrySet()) {
                Label domainEntryLabel = new Label(domainEntry.getKey() + ": " + domainEntry.getValue());
                domainEntryLabel.setFont(new Font("Verdana", 16));
                variablesLabels.add(domainEntryLabel);
            }
        }
    }

    private void updateLabels() {
        int j = 0;
        for (Question currentQuestion : questionList) {
            for (Map.Entry<String, String> domainEntry : currentQuestion.getDomainEntry().entrySet()) {
                String newText = domainEntry.getKey() + ": " + domainEntry.getValue();
                variablesLabels.get(j).setText(newText);
                j++;
            }
        }
    }


    private void showEndScene(ActionEvent event) throws IOException {
        Parent endSceneParent = FXMLLoader.load(getClass().getResource("endScene.fxml"));
        Scene endScene = new Scene(endSceneParent);

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(endScene);
        appStage.show();

    }

    @FXML
    private void restartApp(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("startScene.fxml"));

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        questionList.clear();
        questionList = QuestionsLoader.load();
        for (Question question : questionList) {
            question.cleanup();
        }
        initialize();

        appStage.close();

        Main main = new Main();
        main.start(new Stage());


    }

}


