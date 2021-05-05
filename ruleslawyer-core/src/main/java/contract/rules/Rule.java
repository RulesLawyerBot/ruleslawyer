package contract.rules;


import contract.Searchable;
import contract.rules.enums.RuleSource;
import contract.searchRequests.SearchRequest;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Rule extends AbstractRule {

    public Rule(String text) {
        this.text = text;
        this.subRules = emptyList();
        this.index = ++ruleCount;
    }

    @Override
    public List<AbstractRule> searchForKeywords(List<String> keywords) {
        return keywords.stream().allMatch(this.text.toLowerCase()::contains) ?
                singletonList(this) :
                emptyList();
    }

    @Override
    public RuleSource getRuleSource() {
        return parentRule.getRuleSource();
    }

    public RuleHeader getHeader() {
        return (RuleHeader)(this.parentRule.parentRule);
    }

    public RuleSubheader getSubHeader() {
        return (RuleSubheader)this.parentRule;
    }

    @Override
    public Integer getRelevancy(List<String> keywords) {
        return super.getRelevancy(keywords) + 2*SUBRULE_RELEVANCY_MODIFIER;
    }

    @Override
    public Integer getFuzzyRelevancy(List<String> keywords, Integer fuzzyDistance) {
        return super.getFuzzyRelevancy(keywords, fuzzyDistance) + 2*SUBRULE_RELEVANCY_MODIFIER;
    }

    @Override
    public List<PrintedRule> getPrintedRules() {
        return singletonList(
                new PrintedRule(
                    this.getRuleSource() + " " + this.getHeader().getText() + " " + this.getSubHeader().getText(),
                    this.getText()
            )
        );
    }
}
