package app.api.pojo;

import contract.rules.citation.Citation;

public class ApiCitation {

    private String citationText;
    private Integer ruleIndex;

    public ApiCitation(String citationText, Integer ruleIndex) {
        this.citationText = citationText;
        this.ruleIndex = ruleIndex;
    }

    public ApiCitation(Citation citation) {
        this.citationText = citation.getCitationText();
        this.ruleIndex = citation.getCitedRule().getIndex();
    }

    public String getCitationText() {
        return citationText;
    }

    public Integer getRuleIndex() {
        return ruleIndex;
    }
}
