package contract.rules;

import contract.rules.enums.RuleSource;
import contract.Searchable;
import contract.searchRequests.RuleSearchRequest;
import contract.searchRequests.SearchRequest;
import exception.NotYetImplementedException;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

public abstract class AbstractRule implements Searchable {
    protected static Integer ruleCount = 0;
    protected static final Integer SUBRULE_RELEVANCY_MODIFIER = 10000;

    protected Integer index;
    protected AbstractRule parentRule;
    protected String text;
    protected List<AbstractRule> subRules;

    public void addAll(List<AbstractRule> rules) {
        subRules.addAll(rules);
        rules.forEach(elem -> elem.parentRule = this);
    }

    public void addAll(AbstractRule ... rules) {
        this.addAll(asList(rules));
    }

    public String getText() {
        return this.text;
    }

    public AbstractRule getParentRule() {
        return this.parentRule;
    }

    public List<AbstractRule> getSubRules() {
        return this.subRules;
    }

    @Override
    public List<AbstractRule> searchForKeywords(SearchRequest searchRequest) {
        if (((RuleSearchRequest)searchRequest).getRuleSource() != ANY_DOCUMENT && ((RuleSearchRequest)searchRequest).getRuleSource() != getRuleSource())
            return emptyList();
        return searchForKeywords(((RuleSearchRequest)searchRequest).getKeywords());
    }

    @Override
    public List<AbstractRule> searchForKeywords(List<String> keywords) {
        List<String> missingKeywords =
                keywords.stream()
                        .filter(keyword -> !this.text.toLowerCase().contains(keyword.toLowerCase()))
                        .filter(keyword -> {
                                try {
                                    return this.getRuleSource() != RuleSource.valueOf(keyword.toUpperCase());
                                } catch (IllegalArgumentException ex) {
                                    return true;
                                }
                            }
                        )
                        .collect(toList());
        if (missingKeywords.size() == 0)
            return singletonList(this);
        return subRules.stream()
                .map(subRule -> subRule.searchForKeywords(missingKeywords))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    @Override
    public List<AbstractRule> fuzzySearchForKeywords(SearchRequest searchRequest, Integer fuzzyDistance) {
        if (((RuleSearchRequest)searchRequest).getRuleSource() != ANY_DOCUMENT && ((RuleSearchRequest)searchRequest).getRuleSource() != getRuleSource())
            return emptyList();
        return fuzzySearchForKeywords(
                searchRequest.getKeywords(),
                new LevenshteinDistance(fuzzyDistance)
        );
    }

    protected List<AbstractRule> fuzzySearchForKeywords(
            List<String> keywords,
            LevenshteinDistance levenshteinDistance
    ) {
        Map<String, Integer> keywordMap = getFuzzySearchMap(
                keywords,
                levenshteinDistance
        );

        return processFuzzySearch(keywordMap, keywords, levenshteinDistance);
    }

    protected List<AbstractRule> fuzzySearchForKeywords(
            List<String> keywords,
            LevenshteinDistance levenshteinDistance,
            Map<String, Integer> parentMap
    ) {
        Map<String, Integer> keywordMap = mergeMaps(
                getFuzzySearchMap(keywords, levenshteinDistance),
                parentMap
        );

        return processFuzzySearch(keywordMap, keywords, levenshteinDistance);
    }

    protected List<AbstractRule> processFuzzySearch(
            Map<String, Integer> keywordMap,
            List<String> keywords,
            LevenshteinDistance levenshteinDistance
    ) {
        if (keywordMap.values().stream().mapToInt(i -> i).sum() <= levenshteinDistance.getThreshold()) {
            return singletonList(this);
        }

        if (subRules == null || subRules.size() == 0) {
            return emptyList();
        }

        return subRules.stream()
                .map(rule -> rule.fuzzySearchForKeywords(keywords, levenshteinDistance, keywordMap))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    protected Map<String, Integer> getFuzzySearchMap(List<String> keywords, LevenshteinDistance levenshteinDistance) {
         return keywords.stream()
                 .collect(toMap(
                         identity(),
                         keyword -> stream(text.toLowerCase().split(" "))
                                 .mapToInt(ruleWord -> {
                                     Integer distance = levenshteinDistance.apply(keyword.toLowerCase(), ruleWord);
                                     return distance < 0 ? 999999 : distance;
                                 })
                                 .min()
                                 .orElse(999999)
                 ));
    }

    private Map<String, Integer> mergeMaps(Map<String, Integer> map1, Map<String, Integer> map2) {
        map2.forEach((key, value) ->
                map1.merge(
                        key, value, (v1, v2) ->
                                v1 < v2 ? v1 : v2
                )
        );
        return map1;
    }

    @Override
    public Integer getRelevancy(List<String> keywords) {
        return keywords.stream()
                .map(this::getRelevancyForKeyword)
                .mapToInt(OptionalInt::getAsInt) //assert exists
                .sum();
    }

    private OptionalInt getRelevancyForKeyword(String keyword) {
        if (getParentText().toLowerCase().contains(keyword)) {
            return OptionalInt.of(getParentText().toLowerCase().indexOf(keyword));
        }
        if (getRuleSource().toString().contains(keyword.toUpperCase())) {
            return OptionalInt.of(0);
        }
        return subRules.stream()
                .map(subRule -> subRule.getRelevancyForKeyword(keyword))
                .filter(OptionalInt::isPresent)
                .mapToInt(OptionalInt::getAsInt)
                .map(i -> i + 10000)
                .min();
    }

    @Override
    public Integer getFuzzyRelevancy(List<String> keywords, Integer fuzzyDistance) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(fuzzyDistance);
        Map<String, Integer> relevancyMap = getFuzzyRelevancyMap(keywords, levenshteinDistance);

        subRules.stream()
                .map(subRule -> subRule.getFuzzyRelevancyMap(keywords, levenshteinDistance))
                .forEach(map ->
                            map.forEach((key, value) ->
                                    relevancyMap.merge(key, value, (v1, v2) -> v1 < v2 ? v1 : v2)
                            )
                );

        return relevancyMap.values().stream().mapToInt(i->i).sum();
    }

    private Map<String, Integer> getFuzzyRelevancyMap(List<String> keywords, LevenshteinDistance levenshteinDistance) {
        return keywords.stream()
                .collect(toMap(
                        identity(),
                        keyword -> {
                            String[] words = text.toLowerCase().split(" ");
                            return range(0, words.length)
                                    .map(i -> {
                                        Integer distance = levenshteinDistance.apply(keyword.toLowerCase(), words[i]);
                                        return distance < 0 ? 999999 * i : distance * i;
                                    })
                                    .min()
                                    .orElse(999999);
                        }
                ));
    }

    private String getParentText() {
        if (parentRule != null)
            return parentRule.getParentText() + " " + text;
        return text;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof AbstractRule))
            throw new NotYetImplementedException();
        return this.index - ((AbstractRule)o).index;
    }

    public abstract RuleSource getRuleSource();

    public abstract List<PrintedRule> getPrintedRules();
}
