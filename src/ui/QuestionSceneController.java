package ui;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import helper.OutputLoader;
import helper.QuestionsLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.RuleProcessor;
import model.AnswerValidationType;
import model.Output;
import model.Question;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

import static ui.StartController.questionList;

public class QuestionSceneController {

    @FXML
    Label question;
    @FXML
    VBox responsePanel;
    @FXML
    Label errorMessage;
    @FXML
    Button nextButton;
    @FXML
    ImageView questionImage;


    public static List<Label> variablesLabels =  Collections.synchronizedList(new ArrayList<>());


    private static int questionNumber = 1;
    public static List<Integer> askedQuestionsIds = new ArrayList<>();
    public static boolean restart = false;
    public static String pathRules = "rules.pdf";
    public static RuleProcessor ruleProcessor = new RuleProcessor();

    @FXML
    public void initialize() {

        responsePanel.setSpacing(10);
        Question currentQuestion = Question.getQuestionById(questionList, questionNumber);
        askedQuestionsIds.add(questionNumber);

        Image image = new Image(getClass().getResource(currentQuestion.getPicturePath()).toExternalForm());

        question.setText(currentQuestion.getName());
        questionImage.setImage(image);

        ToggleGroup answersGroup = new ToggleGroup();

        if (questionNumber == 34) {
            nextButton.setText("Results");
        }

        for (Map.Entry<Integer, String> currentAnswer : currentQuestion.getPossibleAnswers().entrySet()) {

            if (currentQuestion.getAnswerType() == AnswerValidationType.SINGLE_ANSWER) {

                RadioButton answer = new RadioButton(currentAnswer.getValue());
                answer.setId(currentAnswer.getKey().toString());
                answer.setFont(Font.font ("System", 18));
                answer.setToggleGroup(answersGroup);
                responsePanel.getChildren().add(answer);
            } else if (currentQuestion.getAnswerType() == AnswerValidationType.MULTIPLE_ANSWER) {

                CheckBox answer = new CheckBox(currentAnswer.getValue());
                answer.setId(currentAnswer.getKey().toString());
                answer.setFont(Font.font ("System", 18));
                answer.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
//                    List<Node> answers = responsePanel.getChildren();
                    List<Node> answers = Collections.synchronizedList(responsePanel.getChildren());

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
            ruleProcessor.setRules();
            ruleProcessor.applyInference();
            updateLabels();
        }

    }

    private void addNoneAnswer(Question currentQuestion) {
        if (currentQuestion.getAnswerType() == AnswerValidationType.MULTIPLE_ANSWER) {
            CheckBox noneAnswer = new CheckBox("None of the above");
            noneAnswer.setFont(Font.font ("System", 18));
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
    public void changeQuestion(ActionEvent event) {

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

            findNextQuestion(currentQuestion, event);
        }
    }

    private void findNextQuestion(Question currentQuestion, ActionEvent event) {

        questionNumber = currentQuestion.evaluatePoints();

        if (questionNumber == 0) {
            showEndScene(event, "endScene.fxml");

        } else if (questionNumber == -1) {
            showResultsPdf(event);
            showEndScene(event, "resultsScene.fxml");

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

        return isValid;
    }

    @FXML
    public void pressDomainButton(ActionEvent event) throws IOException {
        Stage domainStage = new Stage();

        domainStage.initModality(Modality.APPLICATION_MODAL);
        domainStage.setTitle("Domain Variables");

        Parent domainParent = FXMLLoader.load(getClass().getResource("domainScene.fxml"));
        Scene domainScene = new Scene(domainParent);
        domainStage.setResizable(false);

        domainStage.setScene(domainScene);
        domainStage.showAndWait();

    }

    private void updateLabels() {
        int j = 0;

        for (Map.Entry<String, String> entry : ruleProcessor.getDomainEntries().entrySet()) {

            String newText = j + ". " + entry.getKey() + ": " + entry.getValue();

            if (j >= variablesLabels.size()) {
                Label newLabel = new Label(newText);
                variablesLabels.add(newLabel);
                newLabel.setFont(new Font("Verdana", 16));
            } else {
                if (!Objects.equals(variablesLabels.get(j).getText(), newText)) {
                    variablesLabels.get(j).setText(newText);

                    if (j <= 47) {
                        variablesLabels.get(j).setStyle("-fx-text-fill: blue");
                    } else if (j <= 55) {
                        variablesLabels.get(j).setStyle("-fx-text-fill: green");
                    } else if (j <= 60) {
                        variablesLabels.get(j).setStyle("-fx-text-fill: orange");
                    } else if (j <= 68) {
                        variablesLabels.get(j).setStyle("-fx-text-fill: red");
                    }
                }
            }
            j++;
        }
    }


    private void showEndScene(ActionEvent event, String fxml) {
        Parent endSceneParent = null;
        try {
            endSceneParent = FXMLLoader.load(getClass().getResource(fxml));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene endScene = new Scene(endSceneParent);

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(endScene);
        appStage.show();

    }

    private void showResultsPdf(ActionEvent event) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);

        String file = "results_" + time + ".pdf";

        PdfDocument pdfDoc = null;
        try {
            pdfDoc = new PdfDocument(new PdfWriter(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Document doc = new Document(pdfDoc);

        Text title = new Text("The results of the gastro-intestinal expert system");
        title.setBold();

        Paragraph paraTitle = new Paragraph(title);
        paraTitle.setTextAlignment(TextAlignment.CENTER);

        Text timeText = new Text(time);

        Paragraph paraTime = new Paragraph(timeText);
        paraTime.setTextAlignment(TextAlignment.CENTER);

        doc.add(paraTitle);
        doc.add(paraTime);
        doc.add(new Paragraph("\n"));

        writeResults(doc);

        doc.close();

    }

    private void writeResults(Document doc) {

        List<Output> outputs = OutputLoader.loadOutputs();

        for (Output output : outputs) {
            if (Objects.equals(ruleProcessor.getDomainEntries().get(output.getVariable()), "true")) {
                Text title = new Text(output.getTitle());
                Paragraph paragraphTitle = new Paragraph(title);
                paragraphTitle.setUnderline();
                Text text = new Text(output.getText());
                Paragraph paragraphText = new Paragraph(text);

                doc.add(paragraphTitle);
                doc.add(paragraphText);
                doc.add(new Paragraph("\n"));
            }
        }
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
        questionList = QuestionsLoader.loadQuestions();
        questionNumber = 1;

        initialize();
        errorMessage.setText("");
    }

    @FXML
    private void openPdf(ActionEvent event) {

        try {
            InputStream inputStream = getClass().getResourceAsStream(pathRules);

            Path path = Paths.get(pathRules);

            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

            Desktop.getDesktop().open(path.toFile());

        } catch (Exception e) {
            System.out.println("bla");
        }

    }


}


