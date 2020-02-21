package contract.rules;

import contract.RuleSource;
import contract.Searchable;
import contract.searchRequests.RuleSearchRequest;
import contract.searchRequests.SearchRequest;
import exception.NotYetImplementedException;

import java.util.Collection;
import java.util.List;

import static contract.RuleSource.ANY;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public abstract class AbstractRule implements Searchable {
    protected static Integer ruleCount = 0;

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

    public List<AbstractRule> getSubRules() {
        return this.subRules;
    }

    @Override
    public List<AbstractRule> searchForKeywords(SearchRequest searchRequest) {
        if (((RuleSearchRequest)searchRequest).getRuleSource() != ANY && ((RuleSearchRequest)searchRequest).getRuleSource() != getRuleSource())
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
    public Integer getRelevancy(List<String> keywords) {
        return keywords.stream()
                .mapToInt(this::getRelevancyForKeyword)
                .sum();
    }

    private Integer getRelevancyForKeyword(String keyword) {
        String fullCitation = getFullCitation().toLowerCase();
        int count = fullCitation.split(keyword.toLowerCase()).length;
        return fullCitation.indexOf(keyword) - (count-1) * fullCitation.length();
    }

    private String getParentText() {
        if (parentRule != null)
            return parentRule.getParentText() + " " + text;
        return this.text;
    }

    private String getFullCitation() {
        return getParentText() + subRules.stream()
                .map(AbstractRule::getFullCitation)
                .collect(joining("\n"));
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof AbstractRule))
            throw new NotYetImplementedException();
        return this.index - ((AbstractRule)o).index;
    }

    public abstract RuleSource getRuleSource();
}
