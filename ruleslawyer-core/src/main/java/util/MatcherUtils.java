package util;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.ahocorasick.trie.Trie.builder;

public class MatcherUtils {

    public static List<String> filterContainsWholeWord(String string, Set<String> patterns) {
        Trie trie = builder()
                .ignoreOverlaps()
                .ignoreCase()
                .addKeywords(patterns)
                .build();

        Collection<Emit> emits = trie.parseText(string);

        return patterns.stream()
                .filter(word -> Arrays.toString(emits.toArray()).contains(word))
                .collect(toList());
    }
}
