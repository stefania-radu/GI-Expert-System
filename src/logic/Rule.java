package logic;

import java.util.Map;
import java.util.Objects;

public class Rule {

    Map<String, String> terms;
    String outputVariable;
    String outputValue;

    public Rule(Map<String, String> terms) {
        this.terms = terms;
    }

    public Map<String, String> getTerms() {
        return terms;
    }

    public boolean canInfer() {

        for (Map.Entry<String, String> term : terms.entrySet()) {
            if (Objects.equals(term.getValue(), "none")) {
                return false;
            }
        }
        return true;

    }
}
