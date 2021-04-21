package app.slack.service;

import app.slack.contract.SlackBlock;
import app.slack.contract.SlackResponse;
import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import contract.searchResults.RawRuleSearchResult;
import contract.searchResults.SearchResult;
import org.springframework.stereotype.Service;
import service.RawRuleSearchService;

import java.util.List;

import static contract.rules.enums.RuleRequestCategory.ANY_RULE_TYPE;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static java.util.Arrays.asList;

@Service
public class SlackSearchService {

    private static final Integer MAX_HEADER_LENGTH = 150;
    private static final Integer MAX_TEXT_LENGTH = 3000;

    private RawRuleSearchService rawRuleSearchService;

    public SlackSearchService() {
        this.rawRuleSearchService = new RawRuleSearchService();
    }

    public SlackResponse searchRules(String query) {
        RuleSearchRequest searchRequest = new RuleSearchRequest(
                asList(query.split(" ")),
                ANY_DOCUMENT,
                0,
                ANY_RULE_TYPE
        );

        RawRuleSearchResult rawResult = rawRuleSearchService.getRawResult(searchRequest);
        return null; //todo
    }

    private List<SlackBlock> getBlocksForResults(List<SearchResult<AbstractRule>> rules) {
        return null;
    }

    private SlackBlock getBlockForRule(AbstractRule rule) {
        return null;
    }
}
