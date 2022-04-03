package ingestion.rule;

import contract.rules.AbstractRule;
import contract.rules.citation.AllowedCitationLink;
import contract.rules.citation.Citation;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.concat;
import static org.ahocorasick.trie.Trie.builder;

public class CitationInitService {
    private static Map<String, List<Citation>> citations = null;
    private static Trie trie = null;

    public static void setOutboundCitations(List<AbstractRule> rules) {
        if (citations == null) {
            getCitations(rules);
            trie = builder()
                    .onlyWholeWords()
                    .ignoreOverlaps()
                    .ignoreCase()
                    .addKeywords(citations.keySet())
                    .build();
        }

        rules.forEach(CitationInitService::setOutboundCitationsForRule);
    }

    public static Set<String> getCitationStrings() {
        return citations.keySet();
    }

    private static void getCitations(List<AbstractRule> rules) {
        citations = rules.stream()
                .flatMap(CitationInitService::getInboundCitationsForRule)
                .filter(citation -> citation.getCitationText().length() > 0)
                .collect(
                        groupingBy(
                                Citation::getCitationText,
                                toList()
                        )
                );
    }

    private static Stream<Citation> getInboundCitationsForRule(AbstractRule rule) {
        return concat(
                rule.getInboundCitations().stream()
                        .map(citation -> new Citation(citation.toLowerCase(), rule)),
                rule.getSubRules().stream()
                        .flatMap(CitationInitService::getInboundCitationsForRule)
        );
    }

    private static void setOutboundCitationsForRule(AbstractRule rule) {
        rule.setOutboundCitations(
                trie.parseText(rule.getText()).stream()
                        .map(Emit::getKeyword)
                        .map(keyword -> citations.get(keyword))
                        .flatMap(Collection::stream)
                        .filter(citation -> AllowedCitationLink.isAllowed(citation.getCitedRule().getRuleSource(), rule.getRuleSource()))
                        .collect(toList())
        );
        rule.getSubRules().forEach(CitationInitService::setOutboundCitationsForRule);
    }
}
