package search.contract.builder;

import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import search.contract.DiscordSearchRequest;

import java.util.ArrayList;
import java.util.List;

import static contract.rules.enums.RuleRequestCategory.ANY_RULE_TYPE;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;

public class DiscordSearchRequestBuilder {

    private String requester;
    private String channelName;
    private List<String> keywords;
    private RuleSource ruleSource;
    private Integer pageNumber;
    private RuleRequestCategory ruleRequestCategory;

    public static DiscordSearchRequestBuilder aDiscordSearchRequest() {
        DiscordSearchRequestBuilder discordSearchRequestBuilder = new DiscordSearchRequestBuilder()
                .setRuleSource(ANY_DOCUMENT)
                .setRuleRequestCategory(ANY_RULE_TYPE)
                .setPageNumber(0)
                .setChannelName("TO BE IMPLEMENTED");
        discordSearchRequestBuilder.keywords = new ArrayList<>();
        return discordSearchRequestBuilder;
    }

    public static DiscordSearchRequestBuilder fromDiscordSearchRequest(DiscordSearchRequest request) {
        return aDiscordSearchRequest()
                .setRequester(request.getRequester())
                .setChannelName(request.getChannelName())
                .appendKeywords(request.getKeywords())
                .setRuleSource(request.getRuleSource())
                .setPageNumber(request.getPageNumber())
                .setRuleRequestCategory(request.getRuleRequestCategory());
    }

    public DiscordSearchRequestBuilder appendKeywords(List<String> keywords) {
        this.keywords.addAll(keywords);
        return this;
    }

    public DiscordSearchRequestBuilder setRequester(String requester) {
        this.requester = requester;
        return this;
    }

    public DiscordSearchRequestBuilder setChannelName(String channelName) {
        this.channelName = channelName;
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

    public DiscordSearchRequest build() {
        return new DiscordSearchRequest(requester, channelName, keywords, ruleSource, pageNumber, ruleRequestCategory);
    }
}
