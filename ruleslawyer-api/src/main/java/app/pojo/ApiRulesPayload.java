package app.pojo;

import contract.searchRequests.RuleSearchRequest;

import java.util.List;

public class ApiRulesPayload {

    private List<ApiNormalizedRule> rules;
    private RuleSearchRequest request;
    private Integer lastPageNumber;

    public ApiRulesPayload(List<ApiNormalizedRule> rules, RuleSearchRequest request, Integer totalPageNumber) {
        this.rules = rules;
        this.request = request;
        this.lastPageNumber = totalPageNumber;
    }

    public List<ApiNormalizedRule> getRules() {
        return rules;
    }

    public RuleSearchRequest getRequest() {
        return request;
    }

    public Integer getLastPageNumber() {
        return lastPageNumber;
    }
}
