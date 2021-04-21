package contract.rules;

import contract.rules.enums.RuleSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

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

    @Override
    public List<PrintedRule> getPrintedRules() {
        if (this.getSubRules().stream().allMatch(subRule -> subRule.getSubRules().size() == 0)) {
            return singletonList(
                    new PrintedRule(
                        this.getRuleSource() + " " + this.getText(),
                        this.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
                )
            );
        }
        return this.getSubRules().stream().map(
                subrule -> new PrintedRule(
                        this.getRuleSource() + " " + this.getText() + " " + subrule.getText(),
                        subrule.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
                )
        )
                .collect(toList());
    }

}
