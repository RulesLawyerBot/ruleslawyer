package app.service;

import app.pojo.ApiNormalizedRule;
import app.pojo.ApiRulesPayload;
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
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

@Service
public class ApiSearchService {

    private SearchRepository<AbstractRule> ruleRepository;
    private SearchRepository<AbstractRule> digitalRuleRepository;
    public static final Integer PAGE_SIZE = 10;

    public ApiSearchService() {
        ruleRepository = new SearchRepository<>(getRules());
        digitalRuleRepository = new SearchRepository<>(getDigitalEventRules());
    }

    public ApiRulesPayload getRuleSearchResults(RuleSearchRequest ruleSearchRequest) {
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
        return new ApiRulesPayload(
                paginateRules(
                    normalizeRules(output),
                    ruleSearchRequest.getPageNumber()
                ),
                ruleSearchRequest,
                output.size() / PAGE_SIZE
        );
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

    private List<ApiNormalizedRule> paginateRules(List<ApiNormalizedRule> rules, Integer pageNumber) {
        if (pageNumber < 0 || pageNumber > (rules.size() / PAGE_SIZE)) {
            return emptyList();
        }
        return pageNumber == (rules.size() / PAGE_SIZE) ?
                rules.subList(pageNumber * PAGE_SIZE, rules.size()) :
                rules.subList(pageNumber * PAGE_SIZE, (pageNumber+1) * PAGE_SIZE);
    }
}
