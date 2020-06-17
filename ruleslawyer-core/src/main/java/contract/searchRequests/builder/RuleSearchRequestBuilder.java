package contract.searchRequests.builder;

import contract.RuleSource;
import contract.searchRequests.RuleSearchRequest;

import java.util.ArrayList;
import java.util.List;

import static contract.RuleSource.ANY;

public class RuleSearchRequestBuilder {

    private RuleSource ruleSource;
    private List<String> keywords;
    private Integer pageNumber;
    private Boolean isDigitalRuleRequest;

    public static RuleSearchRequestBuilder aRuleSearchRequest() {
        return new RuleSearchRequestBuilder();
    }

    public static RuleSearchRequestBuilder fromRuleSearchRequest(RuleSearchRequest ruleSearchRequest) {
        return aRuleSearchRequest()
                .setRuleSource(ruleSearchRequest.getRuleSource())
                .setPageNumber(ruleSearchRequest.getPageNumber())
                .appendKeywords(ruleSearchRequest.getKeywords())
                .setIsDigitalRuleRequest(ruleSearchRequest.isDigitalRuleRequest());
    }

    private RuleSearchRequestBuilder() {
        ruleSource = ANY;
        pageNumber = 0;
        keywords = new ArrayList<>();
        isDigitalRuleRequest = false;
    }

    public RuleSearchRequestBuilder setRuleSource(RuleSource ruleSource) {
        this.ruleSource = ruleSource;
        return this;
    }

    public RuleSearchRequestBuilder appendKeywords(List<String> keywords) {
        this.keywords.addAll(keywords);
        return this;
    }

    public RuleSearchRequestBuilder setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public RuleSearchRequestBuilder setIsDigitalRuleRequest(Boolean isDigitalRuleRequest) {
        this.isDigitalRuleRequest = isDigitalRuleRequest;
        return this;
    }

    public RuleSearchRequest build() {
        return new RuleSearchRequest(keywords, ruleSource, pageNumber, isDigitalRuleRequest);
    }
}
