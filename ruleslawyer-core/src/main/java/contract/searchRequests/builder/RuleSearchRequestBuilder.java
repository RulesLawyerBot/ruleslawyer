package contract.searchRequests.builder;

import contract.RuleSource;
import contract.searchRequests.RuleSearchRequest;

import java.util.List;

import static contract.RuleSource.ANY;

public class RuleSearchRequestBuilder {

    private RuleSource ruleSource;
    private List<String> keywords;
    private Integer pageNumber;

    public static RuleSearchRequestBuilder aRuleSearchRequest() {
        return new RuleSearchRequestBuilder();
    }

    private RuleSearchRequestBuilder() {
        ruleSource = ANY;
        pageNumber = 0;
    }

    public RuleSearchRequestBuilder setRuleSource(RuleSource ruleSource) {
        this.ruleSource = ruleSource;
        return this;
    }

    public RuleSearchRequestBuilder setKeywords(List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public RuleSearchRequestBuilder setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public RuleSearchRequest build() {
        return new RuleSearchRequest(keywords, ruleSource, pageNumber);
    }

}
