package app.api.service;

import app.api.pojo.ApiCitation;
import app.api.pojo.ApiNormalizedRule;
import app.api.pojo.ApiRulesPayload;
import contract.rules.AbstractRule;
import contract.rules.citation.Citation;
import contract.rules.enums.RuleSource;
import contract.searchRequests.RuleSearchRequest;
import contract.searchResults.RawRuleSearchResult;
import contract.searchResults.SearchResult;
import exception.NotYetImplementedException;
import org.springframework.stereotype.Service;
import service.RawRuleSearchService;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static contract.rules.enums.RuleRequestCategory.PAPER;
import static contract.rules.enums.RuleSource.DIPG;
import static contract.rules.enums.RuleSource.DMTR;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

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
            return new ApiRulesPayload(
                    emptyList(),
                    ruleSearchRequest,
                    ruleSearchRequest.getRuleRequestCategory() == DIGITAL ? DIGITAL : PAPER,
                    false,
                    false
            );
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
                getParentText(rule),
                getParentIndices(rule),
                rule.getText(),
                normalizeRules(rule.getSubRules()),
                rule.getRuleSource(),
                rule.getIndex(),
                getPreviousRule(rule),
                getNextRule(rule),
                getApiCitationsFromCitations(rule.getOutboundCitations())
        );
    }

    private List<String> getParentText(AbstractRule rule) {
        if (rule.getParentRule() == null) {
            return emptyList();
        }
        if (rule.getParentRule().getParentRule() == null) {
            return singletonList(rule.getParentRule().getText());
        }
        return asList(rule.getParentRule().getParentRule().getText(), rule.getParentRule().getText());
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

    private Integer getPreviousRule(AbstractRule rule) {
        if (rule.getParentRule() == null) {
            Optional<AbstractRule> previousRule = rawRuleSearchService.findByIndex(rule.getIndex()-1);
            if (!previousRule.isPresent()) {
                return null;
            } else {
                List<Integer> parentIndices = getParentIndices(previousRule.get());
                return parentIndices.size() == 0 ?
                        previousRule.get().getIndex() :
                        parentIndices.get(parentIndices.size() - 1);
            }
        } else {
            List<AbstractRule> siblingRules = rule.getParentRule().getSubRules();
            OptionalInt index = range(0, siblingRules.size())
                    .filter(ind -> siblingRules.get(ind).getIndex().equals(rule.getIndex()))
                    .findAny();
            return !index.isPresent() || index.getAsInt() == 0 ?
                    null :
                    siblingRules.get(index.getAsInt()-1).getIndex();
        }
    }

    private Integer getNextRule(AbstractRule rule) {
        if (rule.getParentRule() == null) {
            Integer maxSubruleIndex = getSubruleMaxIndex(rule);
            return rawRuleSearchService.findByIndex(maxSubruleIndex+1).isPresent() ?
                    maxSubruleIndex + 1 :
                    null;
        } else {
            List<AbstractRule> siblingRules = rule.getParentRule().getSubRules();
            OptionalInt index = range(0, siblingRules.size())
                    .filter(ind -> siblingRules.get(ind).getIndex().equals(rule.getIndex()))
                    .findAny();
            return !index.isPresent() || index.getAsInt() == siblingRules.size()-1 ?
                    null :
                    siblingRules.get(index.getAsInt()+1).getIndex();
        }
    }

    private Integer getSubruleMaxIndex(AbstractRule rule) {
        return rule.getSubRules().stream()
                .mapToInt(this::getSubruleMaxIndex)
                .max()
                .orElse(rule.getIndex());
    }

    public List<ApiNormalizedRule> getRuleIndex() {
        return getRuleIndexStream()
                .collect(toList());
    }

    public List<ApiNormalizedRule> getRuleIndex(RuleSource ruleSource) {
        return getRuleIndexStream()
                .filter(rule -> rule.getRuleSource() == ruleSource)
                .collect(toList());
    }

    private Stream<ApiNormalizedRule> getRuleIndexStream() {
        return rawRuleSearchService.getSearchSpace().stream()
                .map(abstractRule ->
                                new ApiNormalizedRule(
                                        null,
                                        emptyList(),
                                        abstractRule.getText(),
                                        null,
                                        abstractRule.getRuleSource(),
                                        abstractRule.getIndex(),
                                        getPreviousRule(abstractRule),
                                        getNextRule(abstractRule),
                                        getApiCitationsFromCitations(abstractRule.getOutboundCitations())
                                )
                );
    }

    private List<ApiCitation> getApiCitationsFromCitations(List<Citation> citations) {
        return citations.stream()
                .map(ApiCitation::new)
                .collect(toList());
    }
}