package service;

import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import contract.searchResults.RawRuleSearchResult;
import contract.searchResults.SearchResult;
import repository.SearchRepository;

import java.util.List;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static contract.rules.enums.RuleRequestCategory.PAPER;
import static ingestion.rule.JsonRuleIngestionService.getRawDigitalRulesData;
import static ingestion.rule.JsonRuleIngestionService.getRawRulesData;

public class RawRuleSearchService {

    SearchRepository<AbstractRule> ruleSearchRepository;
    SearchRepository<AbstractRule> digitalRuleSearchRepository;

    public RawRuleSearchService() {
        ruleSearchRepository = new SearchRepository<>(getRawRulesData());
        digitalRuleSearchRepository = new SearchRepository<>(getRawDigitalRulesData());
    }

    public RawRuleSearchResult getRawResult(RuleSearchRequest request) {
        List<SearchResult<AbstractRule>> paperResult = getRawPaperResult(request);
        List<SearchResult<AbstractRule>> digitalResult = getRawDigitalResult(request);
        if (digitalResult.size() == 0) {
            return new RawRuleSearchResult(paperResult, PAPER, false);
        } else {
            if (paperResult.size() == 0) {
                return new RawRuleSearchResult(digitalResult, DIGITAL, false);
            }
            return new RawRuleSearchResult(paperResult, PAPER, true);
        }
    }

    public List<SearchResult<AbstractRule>> getRawPaperResult(RuleSearchRequest request) {
        return ruleSearchRepository.getSearchResult(request);
    }

    public List<SearchResult<AbstractRule>> getRawDigitalResult(RuleSearchRequest request) {
        return digitalRuleSearchRepository.getSearchResult(request);
    }
}
