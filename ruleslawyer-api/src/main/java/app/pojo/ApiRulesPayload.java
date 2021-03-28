package app.pojo;

import contract.searchRequests.RuleSearchRequest;

import java.util.List;

public class ApiRulesPayload {

    private List<ApiNormalizedRule> rules;
    private RuleSearchRequest request;

    public ApiRulesPayload(List<ApiNormalizedRule> rules, RuleSearchRequest request) {
        this.rules = rules;
        this.request = request;
    }

    public List<ApiNormalizedRule> getRules() {
        return rules;
    }

    public RuleSearchRequest getRequest() {
        return request;
    }
}
