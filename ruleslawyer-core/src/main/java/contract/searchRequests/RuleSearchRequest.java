package contract.searchRequests;

import contract.RuleSource;
import contract.rules.AbstractRule;

import java.util.List;
import java.util.Objects;

import static contract.RuleSource.ANY;
import static java.lang.String.join;

public class RuleSearchRequest extends SearchRequest<AbstractRule> {
    public RuleSearchRequest(List<String> keywords, RuleSource ruleSource, Integer pageNumber, Boolean isDigitalRuleRequest) {
        this.keywords = keywords;
        this.ruleSource = ruleSource;
        this.pageNumber = pageNumber;
        this.isDigitalRuleRequest = isDigitalRuleRequest;
    }

    private RuleSource ruleSource;
    private Boolean isDigitalRuleRequest;

    public RuleSource getRuleSource() {
        return ruleSource;
    }

    public boolean isDigitalRuleRequest() {
        return isDigitalRuleRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleSearchRequest)) return false;
        RuleSearchRequest that = (RuleSearchRequest) o;
        return ruleSource == that.ruleSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleSource);
    }

    @Override
    public String toString() {
        String baseString = "Search keywords: \"" + join("\" \"", getKeywords()) + "\"";
        if (getPageNumber() != 0) {
            baseString += " - Page number: " + getPageNumber();
        }
        if (getRuleSource() != ANY) {
            baseString += " - Displaying results filtered to: " + getRuleSource();
        }
        return baseString;
    }
}
