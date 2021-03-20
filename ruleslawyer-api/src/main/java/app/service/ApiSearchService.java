package app.service;

import app.pojo.ApiNormalizedRule;
import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import contract.searchResults.SearchResult;
import org.springframework.stereotype.Service;
import repository.SearchRepository;

import java.util.List;
import java.util.Optional;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static ingestion.rule.JsonRuleIngestionService.getDigitalEventRules;
import static ingestion.rule.JsonRuleIngestionService.getRules;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

@Service
public class ApiSearchService {

    private SearchRepository<AbstractRule> ruleRepository;
    private SearchRepository<AbstractRule> digitalRuleRepository;

    public ApiSearchService() {
        ruleRepository = new SearchRepository<>(getRules());
        digitalRuleRepository = new SearchRepository<>(getDigitalEventRules());
    }

    public List<ApiNormalizedRule> getRuleSearchResults(RuleSearchRequest ruleSearchRequest) {
        List<AbstractRule> output;
        if (ruleSearchRequest.getRuleRequestCategory() == DIGITAL) {
            output = digitalRuleRepository.getSearchResult(ruleSearchRequest)
                    .stream()
                    .map(SearchResult::getEntry)
                    .collect(toList());
        } else {
            output = ruleRepository.getSearchResult(ruleSearchRequest)
                    .stream()
                    .map(SearchResult::getEntry)
                    .collect(toList());
        }
        return normalizeRules(output);
    }

    private List<ApiNormalizedRule> normalizeRules(List<AbstractRule> rules) {
        return rules.stream()
                .map(this::normalizeRule)
                .collect(toList());
    }

    private ApiNormalizedRule normalizeRule(AbstractRule rule) {
        return new ApiNormalizedRule(
                normalizeRules(rule.getSubRules()),
                rule.getText(),
                getParentText(rule).orElse(null),
                rule.getRuleSource()
        );
    }

    private Optional<String> getParentText(AbstractRule rule) {
        if (rule.getParentRule() == null) {
            return empty();
        }

        return Optional.of(
                getParentText(rule.getParentRule())
                        .map(parentRule ->
                                parentRule + " " + rule.getParentRule().getText()
                        )
                        .orElse(rule.getParentRule().getText())
        );
    }
}
