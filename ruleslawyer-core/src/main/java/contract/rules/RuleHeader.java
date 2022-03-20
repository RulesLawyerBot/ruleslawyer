package contract.rules;

import contract.rules.enums.RuleSource;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class RuleHeader extends AbstractRule {

    private RuleSource ruleSource;

    public RuleHeader(String text, RuleSource ruleSource, List<String> citations) {
        this.text = text;
        this.parentRule = null;
        this.subRules = new ArrayList<>();
        this.index = ++ruleCount;
        this.ruleSource = ruleSource;
        this.inboundCitations = citations;
    }

    @Override
    public RuleSource getRuleSource() {
        return ruleSource;
    }

    @Override
    public List<PrintableRule> getPrintedRules() {
        if (this.getSubRules().stream().allMatch(subRule -> subRule.getSubRules().size() == 0)) {
            return singletonList(
                    new PrintableRule(
                        this.getRuleSource() + " " + this.getText(),
                        this.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
                )
            );
        }
        List<PrintableRule> subRuleList = this.getSubRules().stream().map(
                subrule -> new PrintableRule(
                        this.getRuleSource() + " " + subrule.getText(),
                        subrule.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
                )
        )
                .collect(toList());
        subRuleList.add(0,
                new PrintableRule(
                        this.getRuleSource() + " " + this.getText().substring(0, this.getText().indexOf(" ")),
                        this.getText().substring(this.getText().indexOf(" "))
                )
        );
        return subRuleList;
    }
}
