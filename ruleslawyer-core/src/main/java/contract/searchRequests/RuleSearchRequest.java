package contract.searchRequests;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import contract.rules.AbstractRule;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;
import static java.util.Objects.hash;

@JsonSerialize(include=NON_NULL)
public class RuleSearchRequest extends SearchRequest<AbstractRule> {
    public RuleSearchRequest(List<String> keywords, RuleSource ruleSource, Integer pageNumber, RuleRequestCategory ruleRequestCategory) {
        this.keywords = keywords;
        this.ruleSource = ruleSource;
        this.pageNumber = pageNumber;
        this.ruleRequestCategory = ruleRequestCategory;
    }

    protected RuleSource ruleSource;
    protected RuleRequestCategory ruleRequestCategory;

    public RuleSource getRuleSource() {
        return ruleSource;
    }

    public RuleRequestCategory getRuleRequestCategory() {
        return ruleRequestCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleSearchRequest)) return false;
        RuleSearchRequest that = (RuleSearchRequest) o;
        return ruleSource == that.ruleSource &&
                ruleRequestCategory == that.ruleRequestCategory;
    }

    @Override
    public int hashCode() {
        return hash(ruleSource, ruleRequestCategory);
    }
}
