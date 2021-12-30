package ui;

import helper.FileWriterHelper;
import helper.QuestionsLoader;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.RuleProcessor;
import model.AnswerValidationType;
import model.Question;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static ui.StartController.questionList;

public class QuestionSceneController {

    @FXML
    Label question;
    @FXML
    VBox responsePanel;
    @FXML
    Label errorMessage;
    @FXML
    ToggleButton rulesButton;

    public static List<Label> variablesLabels = new ArrayList<>();

    private static int questionNumber = 1;
    public static List<Integer> askedQuestionsIds = new ArrayList<>();
    public static boolean restart = false;
    public static String pathRules = "conf/rules.pdf";
    public static RuleProcessor ruleProcessor = new RuleProcessor();

    @FXML
    public void initialize() {

        responsePanel.setSpacing(10);
        Question currentQuestion = Question.getQuestionById(questionList, questionNumber);
        askedQuestionsIds.add(questionNumber);

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

        if (questionNumber == 1) {
            variablesLabels.clear();
            initLabels();
        }
//        if (restart) {
//            variablesLabels.clear();
//            initLabels();
//        }
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

            ruleProcessor.applyInference();
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
        int j = 0, qNr = 1;
        List<Integer> ids = new ArrayList<>();

        for (Map.Entry<String, String> entry : ruleProcessor.getDomainEntries().entrySet()) {
            String newText = entry.getKey() + ": " + entry.getValue();
            if (j >= variablesLabels.size()) {
                Label newLabel = new Label(entry.getKey() + ": " + entry.getValue());
                variablesLabels.add(newLabel);
            } else {
                variablesLabels.get(j).setText(newText);
            }

            j++;
        }

//        for (Question currentQuestion : questionList) {
//            StringBuilder explainLabel = new StringBuilder();
//
//            for (Map.Entry<String, String> domainEntry : currentQuestion.getDomainEntry().entrySet()) {
//                String newText = domainEntry.getKey() + ": " + domainEntry.getValue();
//
//                if (askedQuestionsIds.contains(qNr)) {
//                    if (currentQuestion.getAnswerType() == AnswerValidationType.SINGLE_ANSWER) {
//                        for (Map.Entry<Integer, Boolean> givenAnswer : currentQuestion.getGivenAnswers().entrySet()) {
//                            if (givenAnswer.getValue()) {
//                                ids.add(givenAnswer.getKey());
//                            }
//                        }
//                        for (Integer id : ids) {
//                            explainLabel.append(currentQuestion.getPossibleAnswers().get(id));
//                            explainLabel.append(", ");
//                        }
//                    }
//
//                    newText += " (" + explainLabel + ")";
//                }
//                variablesLabels.get(j).setText(newText);
//
//                j++;
//            }
//            qNr++;
//            ids.clear();
//        }
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
        restart = true;

        reloadModel();

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.close();

        Main main = new Main();
        main.start(new Stage());

    }

    private void reloadModel() {
        questionList.clear();
        questionList = QuestionsLoader.load();
        questionNumber = 1;
//        askedQuestionsIds.clear();

        initialize();
        errorMessage.setText("");
    }

    @FXML
    private void openPdf(ActionEvent event) {
        File rulesFile = new File(pathRules);
        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(rulesFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


}


