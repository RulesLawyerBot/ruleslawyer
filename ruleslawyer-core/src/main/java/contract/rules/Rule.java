package contract.rules;


import contract.rules.enums.RuleSource;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Rule extends AbstractRule {

    public Rule(String text, List<String> inboundCitations) {
        this.text = text;
        this.subRules = emptyList();
        this.index = ++ruleCount;
        this.inboundCitations = inboundCitations;
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
    public List<PrintableRule> getPrintedRules() {
        return singletonList(
                new PrintableRule(
                    this.getRuleSource() + " " + this.getHeader().getText() + " | " + this.getSubHeader().getText(),
                    this.getText()
            )
        );
    }
}
