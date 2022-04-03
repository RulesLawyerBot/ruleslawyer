package contract.rules.citation;

import contract.rules.AbstractRule;

public class Citation {

    private String citationText;
    private AbstractRule citedRule;

    public Citation (String citationText, AbstractRule citedRule) {
        this.citationText = citationText;
        this.citedRule = citedRule;
    }

    public String getCitationText() {
        return citationText;
    }

    public AbstractRule getCitedRule() {
        return citedRule;
    }
}
