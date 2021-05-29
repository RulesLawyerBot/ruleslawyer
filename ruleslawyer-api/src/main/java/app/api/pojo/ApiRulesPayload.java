package app.api.pojo;

import contract.rules.enums.RuleRequestCategory;
import contract.searchRequests.RuleSearchRequest;

import java.util.List;

public class ApiRulesPayload {

    private List<ApiNormalizedRule> rules;
    private RuleSearchRequest request;
    private RuleRequestCategory ruleSourceCategory;
    private boolean hasOtherRuleType;
    private boolean isFuzzyResult;

    public ApiRulesPayload(List<ApiNormalizedRule> rules, RuleSearchRequest request, RuleRequestCategory ruleSourceCategory, boolean hasOtherRuleType, boolean isFuzzyResult) {
        this.rules = rules;
        this.request = request;
        this.ruleSourceCategory = ruleSourceCategory;
        this.hasOtherRuleType = hasOtherRuleType;
        this.isFuzzyResult = isFuzzyResult;
    }

    public List<ApiNormalizedRule> getRules() {
        return rules;
    }

    public RuleSearchRequest getRequest() {
        return request;
    }

    public RuleRequestCategory getRuleSourceCategory() {
        return ruleSourceCategory;
    }

    public boolean isHasOtherRuleType() {
        return hasOtherRuleType;
    }

    public boolean isFuzzyResult() {
        return isFuzzyResult;
    }
}
