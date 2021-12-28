package helper;

import model.Question;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class QuestionsLoader {

    private static List<Question> questions = new ArrayList<>();
    private static final int NR_QUESTIONS = 4;

    public static List<Question> load() {
        if (questions.isEmpty()) {

            try (FileInputStream inputStream = new FileInputStream("conf/questions.properties")) {

                Properties properties = new Properties();
                properties.load(inputStream);
                for (int i = 1; i <= NR_QUESTIONS; i++) {
                    Map<Integer, String> answerTextByIds = new HashMap<>();
                    Map<Integer, Integer> answerWeightByIds = new HashMap<>();
                    Question question = new Question(i);
                    int nrAnswers = Integer.parseInt(properties.getProperty("question" + i + ".responseNumber"));
                    for (int j = 1; j <= nrAnswers; j++) {
                        String answerText = properties.getProperty("question" + i + ".response" + j + ".text");
                        answerTextByIds.put(j, answerText);
                        String answerWeight = properties.getProperty("question" + i + ".response" + j + ".weight");
                        answerWeightByIds.put(j, Integer.parseInt(answerWeight));
                    }
                    question.setName(properties.getProperty("question" + i + ".text"));
                    question.setPossibleAnswers(answerTextByIds);
                    question.setWeights(answerWeightByIds);
                    question.setNextQuestionIdIfTrue(Integer.parseInt(properties.getProperty("question" + i + ".ifTrue")));
                    question.setNextQuestionIdIfFalse(Integer.parseInt(properties.getProperty("question" + i + ".ifFalse")));
                    question.setRequiredPoints(Integer.parseInt(properties.getProperty("question" + i + ".threshold")));
                    question.setVariableName(properties.getProperty("question" + i + ".variable"));
                    question.setVariableType(properties.getProperty("question" + i + ".variable.type"));
                    question.setVariableValue(properties.getProperty("question" + i + ".variable.value"));
                    questions.add(question);

                }
                return questions;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return questions;
        }
    }
}
