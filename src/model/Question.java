package model;

import java.util.List;
import java.util.Map;

public class Question {

    private String name;
    private final Integer questionId;
    private Integer nextQuestionIdIfTrue;
    private Integer nextQuestionIdIfFalse;
    private Map<Integer, String> possibleAnswers;
    private Map<Integer, Boolean> givenAnswers;
    private Map<Integer, Integer> weights;
    private Integer requiredPoints;
    private Map<String, String> domainEntry;
    private AnswerValidationType answerType;
    private String picturePath;

    public Question(Integer questionId) {
        this.questionId = questionId;
    }

    public static Question getQuestionById(List<Question> questionList, int id) {
        for (Question question : questionList) {
            if (question.getQuestionId() == id) {
                return question;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public Integer getNextQuestionIdIfTrue() {
        return nextQuestionIdIfTrue;
    }

    public void setNextQuestionIdIfTrue(Integer nextQuestionIdIfTrue) {
        this.nextQuestionIdIfTrue = nextQuestionIdIfTrue;
    }

    public Integer getNextQuestionIdIfFalse() {
        return nextQuestionIdIfFalse;
    }

    public void setNextQuestionIdIfFalse(Integer nextQuestionIdIfFalse) {
        this.nextQuestionIdIfFalse = nextQuestionIdIfFalse;
    }

    public Map<Integer, String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(Map<Integer, String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public Map<Integer, Boolean> getGivenAnswers() {
        return givenAnswers;
    }

    public void setGivenAnswers(Map<Integer, Boolean> givenAnswers) {
        this.givenAnswers = givenAnswers;
    }

    public Map<Integer, Integer> getWeights() {
        return weights;
    }

    public void setWeights(Map<Integer, Integer> weights) {
        this.weights = weights;
    }

    public Integer getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(Integer requiredPoints) {
        this.requiredPoints = requiredPoints;
    }


    public Map<String, String> getDomainEntry() {
        return domainEntry;
    }

    public void setDomainEntry(Map<String, String> domainEntry) {
        this.domainEntry = domainEntry;
    }

    public AnswerValidationType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerValidationType answerType) {
        this.answerType = answerType;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    @Override
    public String toString() {
        return "Question{" +
                "name='" + name + '\'' +
                ", questionId=" + questionId +
                ", nextQuestionIdIfTrue=" + nextQuestionIdIfTrue +
                ", nextQuestionIdIfFalse=" + nextQuestionIdIfFalse +
                ", possibleAnswers=" + possibleAnswers +
                ", givenAnswers=" + givenAnswers +
                ", weights=" + weights +
                ", requiredPoints=" + requiredPoints +
                ", domainEntry=" + domainEntry +
                ", answerType=" + answerType +
                '}';
    }

    public int evaluatePoints() {

        int sum = 0;

        for (Map.Entry<Integer, Boolean> givenAnswer : givenAnswers.entrySet()) {
            if (givenAnswer.getValue()) {
                if (givenAnswer.getKey() != -1) {
                    sum += weights.get(givenAnswer.getKey());
                }
            }
        }

        if (sum >= requiredPoints) {
            return nextQuestionIdIfTrue;
        } else {
            return nextQuestionIdIfFalse;
        }
    }
}
