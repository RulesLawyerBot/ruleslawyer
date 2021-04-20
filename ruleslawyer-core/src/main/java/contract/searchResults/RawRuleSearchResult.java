package contract.searchResults;

import contract.rules.AbstractRule;
import contract.rules.enums.RuleRequestCategory;

import java.util.List;

public class RawRuleSearchResult {

    private List<SearchResult<AbstractRule>> rawResults;
    private RuleRequestCategory ruleRequestCategory;
    private boolean hasOtherCategory;


    public RawRuleSearchResult(List<SearchResult<AbstractRule>> rawResults, RuleRequestCategory ruleRequestCategory, boolean hasOtherCategory) {
        this.rawResults = rawResults;
        this.ruleRequestCategory = ruleRequestCategory;
        this.hasOtherCategory = hasOtherCategory;
    }

    public List<SearchResult<AbstractRule>> getRawResults() {
        return rawResults;
    }

    public RuleRequestCategory getRuleRequestCategory() {
        return ruleRequestCategory;
    }

    public boolean hasOtherCategory() {
        return hasOtherCategory;
    }
}
