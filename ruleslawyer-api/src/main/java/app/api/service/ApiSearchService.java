package app.api.service;

import app.api.pojo.ApiNormalizedRule;
import app.api.pojo.ApiRulesPayload;
import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import contract.searchResults.SearchResult;
import org.springframework.stereotype.Service;
import service.RawRuleSearchService;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

@Service
public class ApiSearchService {

    private RawRuleSearchService rawRuleSearchService;

    public ApiSearchService() {
        this.rawRuleSearchService = new RawRuleSearchService();
    }

    public ApiRulesPayload getRuleSearchResults(RuleSearchRequest ruleSearchRequest) {
        List<AbstractRule> rawOutput = getRawResults(ruleSearchRequest);
        return new ApiRulesPayload(
                normalizeRules(rawOutput),
                ruleSearchRequest
        );
    }

    public ApiNormalizedRule getCitation(RuleSearchRequest ruleSearchRequest) {
        List<AbstractRule> rawOutput = getRawResults(ruleSearchRequest);
        if (rawOutput.size() == 0) {
            return null;
        }
        return normalizeRule(rawOutput.get(0));
    }

    public List<AbstractRule> getRawResults(RuleSearchRequest ruleSearchRequest) {
        return rawRuleSearchService.getRawResult(ruleSearchRequest)
                .getRawResults()
                .stream()
                .map(SearchResult::getEntry)
                .collect(toList());
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
