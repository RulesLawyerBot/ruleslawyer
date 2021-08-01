package search.contract.request.builder;

import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import search.contract.request.DiscordRuleSearchRequest;

import java.util.ArrayList;
import java.util.List;

import static contract.rules.enums.RuleRequestCategory.ANY_RULE_TYPE;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;

public class DiscordSearchRequestBuilder {

    private String requester;
    private List<String> keywords;
    private RuleSource ruleSource;
    private Integer pageNumber;
    private RuleRequestCategory ruleRequestCategory;

    public static DiscordSearchRequestBuilder aDiscordSearchRequest() {
        DiscordSearchRequestBuilder discordSearchRequestBuilder = new DiscordSearchRequestBuilder()
                .setRuleSource(ANY_DOCUMENT)
                .setRuleRequestCategory(ANY_RULE_TYPE)
                .setPageNumber(0);
        discordSearchRequestBuilder.keywords = new ArrayList<>();
        return discordSearchRequestBuilder;
    }

    public DiscordSearchRequestBuilder appendKeywords(List<String> keywords) {
        this.keywords.addAll(keywords);
        return this;
    }

    public DiscordSearchRequestBuilder setRequester(String requester) {
        this.requester = requester;
        return this;
    }

    public DiscordSearchRequestBuilder setRuleSource(RuleSource ruleSource) {
        this.ruleSource = ruleSource;
        return this;
    }

    public DiscordSearchRequestBuilder setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public DiscordSearchRequestBuilder setRuleRequestCategory(RuleRequestCategory ruleRequestCategory) {
        this.ruleRequestCategory = ruleRequestCategory;
        return this;
    }

    public DiscordRuleSearchRequest build() {
        return new DiscordRuleSearchRequest(requester, keywords, ruleSource, pageNumber, ruleRequestCategory);
    }
}
