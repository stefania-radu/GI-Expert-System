package logic;

import model.Question;
import ui.StartController;

import java.util.*;

public class RuleProcessor {

    private Map<String, String> domainEntries = new LinkedHashMap<>();
    private List<Rule> rules = new ArrayList<>();
    private List<LinkedHashMap<String, String>> maps = new ArrayList<>();

    private static int NR_RULES = 9;

    public Map<String, String> getDomainEntries() {
        return domainEntries;
    }

    public void applyInference() {
        setDomainEntries();
        setRules();

        for (int i = 0; i < rules.size(); i++) {
            switch (i) {
                case 0:
                    if (rules.get(i).canInfer()) {
                        if (Boolean.parseBoolean(domainEntries.get("diabetes")) && Boolean.parseBoolean(String.valueOf(Objects.equals(domainEntries.get("diabetes-onset"), "true")))) {
                            domainEntries.put("diabetes-recent", "true");
                        } else {
                            domainEntries.put("diabetes-recent", "false");
                        }
                    } else {
                        domainEntries.put("diabetes-recent", "none");
                    }
                    setRules();
                    break;

                case 1:
                    if (rules.get(i).canInfer()) {
                        if (Boolean.parseBoolean(domainEntries.get("abdominal-pain-strength")) || Boolean.parseBoolean(String.valueOf(Objects.equals(domainEntries.get("abdominal-pain-medication-improve"), "false")))
                        || (Boolean.parseBoolean(domainEntries.get("abdominal-pain-duration")) && !Boolean.parseBoolean(domainEntries.get("abdominal-pain-food-related"))) ) {
                            domainEntries.put("abdominal-pain-severity", "true");
                        } else {
                            domainEntries.put("abdominal-pain-severity", "false");
                        }
                    } else {
                            domainEntries.put("abdominal-pain-severity", "none");

                    }
                    setRules();
                    break;

                case 2:
                    if (rules.get(i).canInfer()) {
                        if ( ((getInt(domainEntries.get("vomit-duration")) + getInt(domainEntries.get("vomit-frequency"))
                                + getInt(String.valueOf(Objects.equals(domainEntries.get("vomit-food-related"), "false")))
                                + getInt(String.valueOf(Objects.equals(domainEntries.get("vomit-medication-improve"), "false"))))) > 1) {
                            domainEntries.put("vomit-severity", "true");
                        } else {
                            domainEntries.put("vomit-severity", "false");
                        }
                    } else {
                        domainEntries.put("vomit-severity", "none");
                    }
                    setRules();
                    break;

                case 3:
                    if (rules.get(i).canInfer()) {
                        if ( ((getInt(domainEntries.get("gastroenteritis-duration")) + getInt(domainEntries.get("gastroenteritis-frequency"))
                                + getInt(String.valueOf(Objects.equals(domainEntries.get("gastroenteritis-food-related"), "false")))
                                + getInt(String.valueOf(Objects.equals(domainEntries.get("gastroenteritis-medication-improve"), "false"))))) > 1) {
                            domainEntries.put("gastroenteritis-severity", "true");
                        } else {
                            domainEntries.put("gastroenteritis-severity", "false");
                        }
                    } else {
                        domainEntries.put("gastroenteritis-severity", "none");
                    }
                    setRules();
                    break;

                case 4:
                    if (rules.get(i).canInfer()) {
                        if ( (getInt(domainEntries.get("constipation-duration")) + getInt(domainEntries.get("constipation-no-pass"))
                                + getInt(String.valueOf(Objects.equals(domainEntries.get("gastroenteritis-medication-improve"), "false"))) ) > 1) {
                            domainEntries.put("constipation-severity", "true");
                        } else {
                            domainEntries.put("constipation-severity", "false");
                        }
                    } else {
                        domainEntries.put("constipation-severity", "none");
                    }
                    setRules();
                    break;

                case 5:
                    if (rules.get(i).canInfer()) {
                        if ( ( getInt(domainEntries.get("fatigue")) + getInt(domainEntries.get("loss-of-appetite"))
                                + getInt(domainEntries.get("pallor")) ) > 1) {
                            domainEntries.put("anemia-suspicion", "true");
                        } else {
                            domainEntries.put("anemia-suspicion", "false");
                        }
                    } else {
                        domainEntries.put("anemia-suspicion", "none");
                    }
                    setRules();
                    break;

                case 6:
                    if (rules.get(i).canInfer()) {
                        if ( Boolean.parseBoolean(domainEntries.get("jaundice")) || (getInt(domainEntries.get("weight-loss")) + getInt(String.valueOf(Objects.equals(domainEntries.get("abdominal-pain-middle"), "true")))
                                + getInt(domainEntries.get("heavy-smoker")) +  getInt(domainEntries.get("heavy-drinker")) + getInt(domainEntries.get("rash"))) > 1)  {
                            domainEntries.put("hepato-suspicion", "true");
                        } else {
                            domainEntries.put("hepato-suspicion", "false");
                        }
                    } else {
                        domainEntries.put("hepato-suspicion", "none");
                    }
                    setRules();
                    break;

                case 7:
                    if (rules.get(i).canInfer()) {
                        if ( Boolean.parseBoolean(domainEntries.get("dysphagia")) || Boolean.parseBoolean(String.valueOf(Objects.equals(domainEntries.get("vomit-blood"), "true")))
                                || Boolean.parseBoolean(String.valueOf(Objects.equals(domainEntries.get("chest-pain-duration"), "true"))) )  {
                            domainEntries.put("esophagus-suspicion", "true");
                        } else {
                            domainEntries.put("esophagus-suspicion", "false");
                        }
                    } else {
                        domainEntries.put("esophagus-suspicion", "none");
                    }
                    setRules();
                    break;

                case 8:
                    if (rules.get(i).canInfer()) {
                        if ( ((getInt(domainEntries.get("age")) + getInt(domainEntries.get("heavy-drinker")) + getInt(domainEntries.get("heavy-smoker"))
                                + getInt(String.valueOf(Objects.equals(domainEntries.get("diabetes-recent"), "false"))) + getInt(domainEntries.get("other-gastro-diseases"))) > 1) ||
                                (Boolean.parseBoolean(String.valueOf(Objects.equals(domainEntries.get("diabetes-recent"), "true"))) || Boolean.parseBoolean(domainEntries.get("cancers-in-family")) ||
                                        Boolean.parseBoolean(domainEntries.get("other-cancers"))) ) {
                            domainEntries.put("history-severity", "true");
                        } else {
                            domainEntries.put("history-severity", "false");
                        }
                    } else {
                        domainEntries.put("history-severity", "none");
                    }
                    setRules();
                    break;



            }

        }


    }

    public void setDomainEntries() {
        for (Question question : StartController.questionList) {
            domainEntries.putAll(question.getDomainEntry());
        }
    }

    private int getInt(String bool) {
        if (Boolean.parseBoolean(bool)) {
            return 1;
        }
        return 0;
    }

    public void setRules() {

        maps.clear();
        rules.clear();
        for (int i = 0; i < NR_RULES; i++) {
            maps.add(i, new LinkedHashMap<>());
        }
        maps.get(0).put("diabetes", domainEntries.get("diabetes"));
        maps.get(0).put("diabetes-onset", domainEntries.get("diabetes-onset"));
        rules.add(0, new Rule(maps.get(0)));

        maps.get(1).put("abdominal-pain-strength", domainEntries.get("abdominal-pain-strength"));
        maps.get(1).put("abdominal-pain-duration", domainEntries.get("abdominal-pain-duration"));
//        maps.get(1).put("abdominal-pain-medication-improve", domainEntries.get("abdominal-pain-medication-improve"));
        maps.get(1).put("abdominal-pain-food-related", domainEntries.get("abdominal-pain-food-related"));
        rules.add(1, new Rule(maps.get(1)));

        maps.get(2).put("vomit-duration", domainEntries.get("vomit-duration"));
        maps.get(2).put("vomit-frequency", domainEntries.get("vomit-frequency"));
        maps.get(2).put("vomit-food-related", domainEntries.get("vomit-food-related"));
//        maps.get(2).put("vomit-medication-improve", domainEntries.get("vomit-medication-improve"));
        rules.add(2, new Rule(maps.get(2)));

        maps.get(3).put("gastroenteritis-duration", domainEntries.get("gastroenteritis-duration"));
        maps.get(3).put("gastroenteritis-frequency", domainEntries.get("gastroenteritis-frequency"));
        maps.get(3).put("gastroenteritis-food-related", domainEntries.get("gastroenteritis-food-related"));
        rules.add(3, new Rule(maps.get(3)));

        maps.get(4).put("constipation-duration", domainEntries.get("constipation-duration"));
        maps.get(4).put("constipation-no-pass", domainEntries.get("constipation-no-pass"));
        rules.add(4, new Rule(maps.get(4)));

        maps.get(5).put("fatigue", domainEntries.get("fatigue"));
        maps.get(5).put("loss-of-appetite", domainEntries.get("loss-of-appetite"));
        maps.get(5).put("pallor", domainEntries.get("pallor"));
        rules.add(5, new Rule(maps.get(5)));

        maps.get(6).put("jaundice", domainEntries.get("jaundice"));
        maps.get(6).put("weight-loss", domainEntries.get("weight-loss"));
//        maps.get(6).put("abdominal-pain-middle", domainEntries.get("abdominal-pain-middle"));
        maps.get(6).put("heavy-drinker", domainEntries.get("heavy-drinker"));
        maps.get(6).put("heavy-smoker", domainEntries.get("heavy-smoker"));
        maps.get(6).put("rash", domainEntries.get("rash"));
        rules.add(6, new Rule(maps.get(6)));

        maps.get(7).put("dysphagia", domainEntries.get("dysphagia"));
//        maps.get(7).put("vomit-blood", domainEntries.get("vomit-blood"));
//        maps.get(7).put("chest-pain-duration", domainEntries.get("chest-pain-duration"));
        rules.add(7, new Rule(maps.get(7)));

        maps.get(8).put("age", domainEntries.get("age"));
        maps.get(8).put("heavy-drinker", domainEntries.get("heavy-drinker"));
        maps.get(8).put("heavy-smoker", domainEntries.get("heavy-smoker"));
//        maps.get(8).put("diabetes-recent", domainEntries.get("diabetes-recent"));
        maps.get(8).put("other-gastro-diseases", domainEntries.get("other-gastro-diseases"));
        maps.get(8).put("other-cancers", domainEntries.get("other-cancers"));
        maps.get(8).put("cancers-in-family", domainEntries.get("cancers-in-family"));
        rules.add(8, new Rule(maps.get(8)));



    }
}
