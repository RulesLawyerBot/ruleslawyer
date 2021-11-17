package app.api.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import contract.rules.AbstractRule;
import contract.rules.enums.RuleSource;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@JsonSerialize(include=NON_NULL)
public class ApiNormalizedRule {

    private List<String> parentText;
    private List<Integer> parentIndices;
    private String text;
    private List<ApiNormalizedRule> subRules;
    private RuleSource ruleSource;
    private Integer ruleIndex;
    private Integer previousIndex;
    private Integer nextIndex;

    public ApiNormalizedRule(List<String> parentText, List<Integer> parentIndices, String text, List<ApiNormalizedRule> subRules, RuleSource ruleSource, Integer ruleIndex, Integer previousIndex, Integer nextIndex) {
        this.parentText = parentText;
        this.parentIndices = parentIndices;
        this.text = text;
        this.subRules = subRules;
        this.ruleSource = ruleSource;
        this.ruleIndex = ruleIndex;
        this.previousIndex = previousIndex;
        this.nextIndex = nextIndex;
    }

    public List<String> getParentText() {
        return parentText;
    }

    public List<Integer> getParentIndices() {
        return parentIndices;
    }

    public String getText() {
        return text;
    }

    public List<ApiNormalizedRule> getSubRules() {
        return subRules;
    }

    public RuleSource getRuleSource() {
        return ruleSource;
    }

    public Integer getRuleIndex() {
        return ruleIndex;
    }

    public Integer getPreviousIndex() {
        return previousIndex;
    }

    public Integer getNextIndex() {
        return nextIndex;
    }
}
