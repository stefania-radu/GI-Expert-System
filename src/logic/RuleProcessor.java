package logic;

import model.Question;
import ui.StartController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RuleProcessor {

    private Map<String, String> domainEntries = new LinkedHashMap<>();

    public Map<String, String> getDomainEntries() {
        return domainEntries;
    }

    public void applyInference() {
        setDomainEntries();

        if (Boolean.parseBoolean(domainEntries.get("diabetes")) && Boolean.parseBoolean(domainEntries.get("diabetes-onset"))) {
            domainEntries.put("diabetes-recent", "true");
        } else if (Objects.equals(domainEntries.get("diabetes"), "null") || Objects.equals(domainEntries.get("diabetes-onset"), "null")) {
            domainEntries.put("diabetes-recent", "null");
        } else {
            domainEntries.put("diabetes-recent", "false");
        }

    }

    private void setDomainEntries() {
        for (Question question : StartController.questionList) {
            domainEntries.putAll(question.getDomainEntry());
        }

    }
}
