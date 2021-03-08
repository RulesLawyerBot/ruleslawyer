package contract.rules;

import contract.rules.enums.RuleSource;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class RuleSubheader extends AbstractRule {

    public RuleSubheader(String text) {
        this.text = text;
        this.subRules = new ArrayList<>();
        this.index = ++ruleCount;
    }

    @Override
    public RuleSource getRuleSource() {
        return parentRule.getRuleSource();
    }

    public RuleHeader getHeader() {
        return (RuleHeader)this.parentRule;
    }

    @Override
    public Integer getRelevancy(List<String> keywords) {
        return super.getRelevancy(keywords) + 10000;
    }
}
