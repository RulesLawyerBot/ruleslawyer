package search.contract.builder;

import contract.RuleSource;
import search.contract.DiscordSearchRequest;

import java.util.ArrayList;
import java.util.List;

import static contract.RuleSource.ANY;

public class DiscordSearchRequestBuilder {

    private String requester;
    private List<String> keywords;
    private RuleSource ruleSource;
    private Integer pageNumber;
    private Boolean isDigitalRuleRequest;

    public static DiscordSearchRequestBuilder aDiscordSearchRequest() {
        DiscordSearchRequestBuilder discordSearchRequestBuilder = new DiscordSearchRequestBuilder()
                .setRuleSource(ANY)
                .setPageNumber(0)
                .setDigitalRuleRequest(false);
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

    public DiscordSearchRequestBuilder setDigitalRuleRequest(Boolean digitalRuleRequest) {
        isDigitalRuleRequest = digitalRuleRequest;
        return this;
    }

    public DiscordSearchRequest build() {
        return new DiscordSearchRequest(requester, keywords, ruleSource, pageNumber, isDigitalRuleRequest);
    }
}
