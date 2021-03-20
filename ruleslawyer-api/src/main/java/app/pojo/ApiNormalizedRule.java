package app.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import contract.rules.enums.RuleSource;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@JsonSerialize(include=NON_NULL)
public class ApiNormalizedRule {

    private String parentText;
    private String text;
    private List<ApiNormalizedRule> subRules;
    private RuleSource ruleSource;

    public ApiNormalizedRule(List<ApiNormalizedRule> subRules, String text, String parentText, RuleSource ruleSource) {
        this.subRules = subRules;
        this.text = text;
        this.parentText = parentText;
        this.ruleSource = ruleSource;
    }

    public List<ApiNormalizedRule> getSubRules() {
        return subRules;
    }

    public String getText() {
        return text;
    }

    public String getParentText() {
        return parentText;
    }

    public RuleSource getRuleSource() {
        return ruleSource;
    }
}
