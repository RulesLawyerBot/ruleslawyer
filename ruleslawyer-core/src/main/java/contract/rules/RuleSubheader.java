package contract.rules;

import contract.rules.enums.RuleSource;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
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

    @Override
    public List<PrintedRule> getPrintedRules() {
        if (this.getSubRules().size() == 0) {
            return singletonList(
                    new PrintedRule(
                            this.getRuleSource() + " " + this.getHeader().getText(),
                            this.getText()
                    )
            );
        } else {
            return singletonList(
                    new PrintedRule(
                            this.getRuleSource() + " " + this.getHeader().getText() + " " + this.getText(),
                            this.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
                    )
            );
        }
    }
}
