package ingestion.rule;

import contract.rules.AbstractRule;
import contract.rules.citation.Citation;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static org.ahocorasick.trie.Trie.builder;

public class CitationFinderService {
    private static Map<String, Citation> citations = null;
    private static Trie trie = null;

    public static void setOutboundCitations(List<AbstractRule> rules) {
        if (citations == null) {
            setCitations(rules);
            trie = builder()
                    .ignoreOverlaps()
                    .ignoreCase()
                    .addKeywords(citations.keySet())
                    .build();
        }

        rules.forEach(CitationFinderService::setOutboundCitationsForRule);
    }

    private static void setCitations(List<AbstractRule> rules) {
        citations = rules.stream()
                .flatMap(CitationFinderService::getInboundCitationsForRule)
                .filter(citation -> citation.getCitationText().length() > 0)
                .collect(
                        toMap(
                                Citation::getCitationText,
                                identity()
                        )
                );
    }

    private static Stream<Citation> getInboundCitationsForRule(AbstractRule rule) {
        return concat(
                rule.getInboundCitations().stream()
                        .map(citation -> new Citation(citation.toLowerCase(), rule)),
                rule.getSubRules().stream()
                        .flatMap(CitationFinderService::getInboundCitationsForRule)
        );
    }

    private static void setOutboundCitationsForRule(AbstractRule rule) {
        rule.setOutboundCitations(
                trie.parseText(rule.getText()).stream()
                        .map(Emit::getKeyword)
                        .map(keyword -> citations.get(keyword))
                        .collect(toList())
        );
        rule.getSubRules().forEach(CitationFinderService::setOutboundCitationsForRule);
    }
}
