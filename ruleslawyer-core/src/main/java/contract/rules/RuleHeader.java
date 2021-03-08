package contract.rules;

import contract.rules.enums.RuleSource;

import java.util.ArrayList;

import static java.util.stream.Collectors.joining;

public class RuleHeader extends AbstractRule {

    private RuleSource ruleSource;

    public RuleHeader(String text, RuleSource ruleSource) {
        this.text = text;
        this.parentRule = null;
        this.subRules = new ArrayList<>();
        this.index = ++ruleCount;
        this.ruleSource = ruleSource;
    }

    @Override
    public RuleSource getRuleSource() {
        return ruleSource;
    }

}
