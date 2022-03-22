package app.api.pojo;

import contract.rules.citation.Citation;
import contract.rules.enums.RuleSource;

public class ApiCitation {

    private String citationText;
    private Integer ruleIndex;
    private RuleSource ruleSource;

    public ApiCitation(Citation citation) {
        this.citationText = citation.getCitationText();
        this.ruleIndex = citation.getCitedRule().getIndex();
        this.ruleSource = citation.getCitedRule().getRuleSource();
    }

    public String getCitationText() {
        return citationText;
    }

    public Integer getRuleIndex() {
        return ruleIndex;
    }

    public RuleSource getRuleSource() {
        return ruleSource;
    }
}
