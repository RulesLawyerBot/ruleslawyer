package contract.searchRequests;

import contract.RuleSource;
import contract.rules.AbstractRule;

import java.util.List;
import java.util.Objects;

public class RuleSearchRequest extends SearchRequest<AbstractRule> {
    public RuleSearchRequest(List<String> keywords, RuleSource ruleSource, Integer pageNumber) {
        this.keywords = keywords;
        this.ruleSource = ruleSource;
        this.pageNumber = pageNumber;
    }

    private RuleSource ruleSource;

    public RuleSource getRuleSource() {
        return ruleSource;
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
}
