package app.api.service;

import app.api.pojo.ApiNormalizedRule;
import app.api.pojo.ApiRulesPayload;
import contract.rules.AbstractRule;
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
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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

    public ApiNormalizedRule getCitation(Integer index) {
        return rawRuleSearchService.findByIndex(index).map(this::normalizeRule).orElse(null);
    }

    private List<ApiNormalizedRule> normalizeRules(List<AbstractRule> rules) {
        return rules.stream()
                .map(this::normalizeRule)
                .collect(toList());
    }

    private ApiNormalizedRule normalizeRule(AbstractRule rule) {
        return new ApiNormalizedRule(
                getParentText(rule).orElse(null),
                getParentIndices(rule),
                rule.getText(),
                normalizeRules(rule.getSubRules()),
                rule.getRuleSource(),
                rule.getIndex()
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

    private List<Integer> getParentIndices(AbstractRule rule) {
        if (rule.getParentRule() == null) {
            return emptyList();
        }

        Integer parentIndex = rule.getParentRule().getIndex();
        return rule.getParentRule().getParentRule() == null ?
                singletonList(parentIndex) :
                asList(parentIndex, rule.getParentRule().getParentRule().getIndex());
    }
}
