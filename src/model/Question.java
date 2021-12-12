package model;

import java.util.List;
import java.util.Map;

public class Question {

    private String name;
    private Integer questionId;
    private Integer nextQuestionIdIfTrue;
    private Integer nextQuestionIdIfFalse;
    private Map<Integer, String> possibleAnswers;
    private Map<Integer, Boolean> givenAnswers;
    private Map<Integer, Integer> weights;
    private Integer requiredPoints;

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
                '}';
    }

    public int evaluatePoints() {

        int sum = 0;

        for (Map.Entry<Integer, Boolean> givenAnswer : givenAnswers.entrySet()) {
            if (givenAnswer.getValue()) {
                sum += weights.get(givenAnswer.getKey());
            }
        }

        if (sum >= requiredPoints) {
            return nextQuestionIdIfTrue;
        } else {
            return nextQuestionIdIfFalse;
        }
    }
}
