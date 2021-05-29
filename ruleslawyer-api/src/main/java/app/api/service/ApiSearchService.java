package app.api.service;

import app.api.pojo.ApiNormalizedRule;
import app.api.pojo.ApiRulesPayload;
import contract.rules.AbstractRule;
import contract.rules.enums.RuleRequestCategory;
import contract.searchRequests.RuleSearchRequest;
import contract.searchResults.RawRuleSearchResult;
import contract.searchResults.SearchResult;
import org.springframework.stereotype.Service;
import service.RawRuleSearchService;

import java.util.List;
import java.util.Optional;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static contract.rules.enums.RuleRequestCategory.PAPER;
import static contract.rules.enums.RuleSource.DIPG;
import static contract.rules.enums.RuleSource.DMTR;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

@Service
public class ApiSearchService {

    private RawRuleSearchService rawRuleSearchService;

    public ApiSearchService() {
        this.rawRuleSearchService = new RawRuleSearchService();
    }

    public ApiRulesPayload getRuleSearchResults(RuleSearchRequest ruleSearchRequest) {
        RawRuleSearchResult rawRuleSearchResult = rawRuleSearchService.getRawResult(ruleSearchRequest);
        List<AbstractRule> abstractRules =
                rawRuleSearchResult.getRawResults()
                        .stream()
                        .map(SearchResult::getEntry)
                        .collect(toList());
        if (rawRuleSearchResult.getRawResults().size() == 0) {
            return null;
        }
        return new ApiRulesPayload(
                normalizeRules(abstractRules),
                ruleSearchRequest,
                abstractRules.get(0).getRuleSource() == DIPG || abstractRules.get(0).getRuleSource() == DMTR ? DIGITAL : PAPER,
                rawRuleSearchResult.hasOtherCategory(),
                rawRuleSearchResult.isFuzzy()
        );
    }

    public ApiNormalizedRule getCitation(RuleSearchRequest ruleSearchRequest) {
        List<AbstractRule> rawOutput = rawRuleSearchService.getRawResult(ruleSearchRequest)
                .getRawResults()
                .stream()
                .map(SearchResult::getEntry)
                .collect(toList());
        if (rawOutput.size() == 0) {
            return null;
        }
        return normalizeRule(rawOutput.get(0));
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
