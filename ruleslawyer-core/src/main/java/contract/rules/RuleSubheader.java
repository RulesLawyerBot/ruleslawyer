package contract.rules;

import contract.rules.enums.RuleSource;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
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
        return super.getRelevancy(keywords) + SUBRULE_RELEVANCY_MODIFIER;
    }

    @Override
    public Integer getFuzzyRelevancy(List<String> keywords, Integer fuzzyDistance) {
        return super.getFuzzyRelevancy(keywords, fuzzyDistance) + SUBRULE_RELEVANCY_MODIFIER;
    }

    @Override
    public List<PrintableRule> getPrintedRules() {
        if (this.getSubRules().size() == 0) {
            return singletonList(
                    new PrintableRule(
                            this.getRuleSource() + " " + this.getHeader().getText(),
                            this.getText()
                    )
            );
        } else {
            if (this.getHeader().getText().contains(" ")) {
                return asList(
                        new PrintableRule(
                                this.getRuleSource() + " " + this.getHeader().getText().substring(0, this.getHeader().getText().indexOf(" ")),
                                this.getHeader().getText().substring(this.getHeader().getText().indexOf(" "))
                        ),
                        new PrintableRule(
                                this.getRuleSource() + " " + this.getText(),
                                this.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
                        )
                );
            } else {
                return singletonList(
                        new PrintableRule(
                                this.getRuleSource() + " " + this.getHeader().getText() + " " + this.getText(),
                                this.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
                        )
                );
            }
        }
    }
}
