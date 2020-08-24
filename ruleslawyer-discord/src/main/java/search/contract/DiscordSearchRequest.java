package search.contract;

import contract.RuleSource;
import contract.searchRequests.RuleSearchRequest;

import java.util.List;

public class DiscordSearchRequest extends RuleSearchRequest {

    private String requester;

    public DiscordSearchRequest(String requester, List<String> keywords, RuleSource ruleSource, Integer pageNumber, Boolean isDigitalRuleRequest) {
        super(keywords, ruleSource, pageNumber, isDigitalRuleRequest);
        this.requester = requester;
    }

    public String getRequester() {
        return requester;
    }
}
