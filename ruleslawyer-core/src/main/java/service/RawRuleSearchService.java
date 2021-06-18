package service;

import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import contract.searchResults.RawRuleSearchResult;
import contract.searchResults.SearchResult;
import repository.SearchRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static contract.rules.enums.RuleRequestCategory.PAPER;
import static ingestion.rule.JsonRuleIngestionService.getRawDigitalRulesData;
import static ingestion.rule.JsonRuleIngestionService.getRawRulesData;
import static java.util.Optional.empty;

public class RawRuleSearchService {

    SearchRepository<AbstractRule> ruleSearchRepository;
    SearchRepository<AbstractRule> digitalRuleSearchRepository;

    public RawRuleSearchService() {
        ruleSearchRepository = new SearchRepository<>(getRawRulesData());
        digitalRuleSearchRepository = new SearchRepository<>(getRawDigitalRulesData());
    }

    public RawRuleSearchService(List<AbstractRule> paperRules, List<AbstractRule> digitalRules) {
        ruleSearchRepository = new SearchRepository<>(paperRules);
        digitalRuleSearchRepository = new SearchRepository<>(digitalRules);
    }

    public RawRuleSearchResult getRawResult(RuleSearchRequest request) {
        List<SearchResult<AbstractRule>> paperResult = getNonFuzzyPaperResult(request);
        List<SearchResult<AbstractRule>> digitalResult = getNonFuzzyDigitalResult(request);
        if (request.getRuleRequestCategory() == PAPER) {
            return new RawRuleSearchResult(paperResult, PAPER, digitalResult.size() > 0, false);
        } else if (request.getRuleRequestCategory() == DIGITAL) {
            return new RawRuleSearchResult(digitalResult, DIGITAL, paperResult.size() > 0, false);
        } else {
            if (digitalResult.size() == 0) {
                if (paperResult.size() == 0) {
                    return getFuzzyResult(request);
                }
                return new RawRuleSearchResult(paperResult, PAPER, false, false);
            } else {
                if (paperResult.size() == 0) {
                    return new RawRuleSearchResult(digitalResult, DIGITAL, false, false);
                }
                return new RawRuleSearchResult(paperResult, PAPER, true, false);
            }
        }
    }

    public RawRuleSearchResult getFuzzyResult(RuleSearchRequest request) {
        List<SearchResult<AbstractRule>> paperResult = getFuzzyPaperResult(request);
        List<SearchResult<AbstractRule>> digitalResult = getFuzzyDigitalResult(request);
        if (digitalResult.size() == 0) {
            return new RawRuleSearchResult(paperResult, PAPER, false, true);
        } else {
            if (paperResult.size() == 0) {
                return new RawRuleSearchResult(digitalResult, DIGITAL, false, true);
            }
            return new RawRuleSearchResult(paperResult, PAPER, true, true);
        }
    }

    private List<SearchResult<AbstractRule>> getNonFuzzyPaperResult(RuleSearchRequest request) {
        return ruleSearchRepository.getSearchResult(request);
    }

    private List<SearchResult<AbstractRule>> getFuzzyPaperResult(RuleSearchRequest request) {
        return ruleSearchRepository.getFuzzySearchResult(request, request.getKeywords().size() * 2);
    }

    private List<SearchResult<AbstractRule>> getNonFuzzyDigitalResult(RuleSearchRequest request) {
        return digitalRuleSearchRepository.getSearchResult(request);
    }

    private List<SearchResult<AbstractRule>> getFuzzyDigitalResult(RuleSearchRequest request) {
        return digitalRuleSearchRepository.getFuzzySearchResult(request, request.getKeywords().size() * 2);
    }

    public Optional<AbstractRule> findByIndex(Integer index) {
        try {
            return Optional.of(ruleSearchRepository.findByIndex(index));
        } catch (NoSuchElementException ex1) {
            try {
                return Optional.of(digitalRuleSearchRepository.findByIndex(index));
            } catch (NoSuchElementException ex2) {
                return empty();
            }
        }
    }
}
