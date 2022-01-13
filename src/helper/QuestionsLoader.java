package helper;

import model.AnswerValidationType;
import model.Question;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class QuestionsLoader {

    private static List<Question> questions = new ArrayList<>();
    private static final int NR_QUESTIONS = 34;

    public static List<Question> loadQuestions() {
        if (questions.isEmpty()) {

            try (InputStream inputStream = QuestionsLoader.class.getResourceAsStream("questions.properties")) {

                Properties properties = new Properties();
                properties.load(inputStream);
                for (int i = 1; i <= NR_QUESTIONS; i++) {
                    Map<Integer, String> answerTextByIds = new LinkedHashMap<>();
                    Map<Integer, Integer> answerWeightByIds = new HashMap<>();
                    Map<String, String> variableByValues = new LinkedHashMap<>();
                    Question question = new Question(i);
                    int nrAnswers = Integer.parseInt(properties.getProperty("question" + i + ".responseNumber"));
                    question.setAnswerType(AnswerValidationType.valueOf(properties.getProperty("question" + i + ".responseType")));

                    if (question.getAnswerType() == AnswerValidationType.SINGLE_ANSWER) {
                        String variable = properties.getProperty("question" + i + ".variable");
                        String value = properties.getProperty("question" + i + ".variable.value");
                        variableByValues.put(variable, value);
                    }

                    for (int j = 1; j <= nrAnswers; j++) {
                        String answerText = properties.getProperty("question" + i + ".response" + j + ".text");
                        answerTextByIds.put(j, answerText);
                        String answerWeight = properties.getProperty("question" + i + ".response" + j + ".weight");
                        answerWeightByIds.put(j, Integer.parseInt(answerWeight));
                        if (question.getAnswerType() == AnswerValidationType.MULTIPLE_ANSWER) {
                            String responseVariable = properties.getProperty("question" + i + ".response" + j + ".variable");
                            String responseValue = properties.getProperty("question" + i + ".response" + j + ".value");
                            variableByValues.put(responseVariable, responseValue);
                        }
                    }
                    question.setName(properties.getProperty("question" + i + ".text"));
                    question.setPossibleAnswers(answerTextByIds);
                    question.setWeights(answerWeightByIds);
                    question.setNextQuestionIdIfTrue(Integer.parseInt(properties.getProperty("question" + i + ".ifTrue")));
                    question.setNextQuestionIdIfFalse(Integer.parseInt(properties.getProperty("question" + i + ".ifFalse")));
                    question.setRequiredPoints(Integer.parseInt(properties.getProperty("question" + i + ".threshold")));
                    question.setDomainEntry(variableByValues);
                    question.setPicturePath(properties.getProperty("question" + i + ".picture"));
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
