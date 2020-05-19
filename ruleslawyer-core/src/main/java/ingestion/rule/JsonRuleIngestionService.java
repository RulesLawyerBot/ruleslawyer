package ingestion.rule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import contract.RuleSource;
import contract.rules.AbstractRule;
import contract.rules.Rule;
import contract.rules.RuleHeader;
import contract.rules.RuleSubheader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static contract.RuleSource.*;
import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class JsonRuleIngestionService {

    public static List<AbstractRule> getRules() {
        try {
            List<AbstractRule> rules = new ArrayList<>();
            rules.addAll(getFlattenedRules("/CR-parsed.json", CR));
            //rules.addAll(getRules("/CRG-parsed.json", CR)); TODO
            rules.addAll(getRules("/JAR-parsed.json", JAR));
            rules.addAll(getRules("/IPG-parsed.json", IPG));
            rules.addAll(getRules("/MTR-parsed.json", MTR));
            return rules;
        } catch (IOException ignored) {
            System.exit(-1);
        }
        return emptyList();
    }

    private static List<AbstractRule> getRules(String filename, RuleSource ruleSource) throws IOException {
        List<JsonMappedRule> rawRules = getJsonMappedRules(filename);
        return rawRules.stream()
                .map(rule -> convertToRuleHeaders(singletonList(rule), ruleSource))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private static List<AbstractRule> getFlattenedRules(String filename, RuleSource ruleSource) throws IOException {
        List<JsonMappedRule> rawRules = getJsonMappedRules(filename);
        return rawRules.stream()
                .map(rule -> convertToRuleHeaders(rule.getSubRules(), ruleSource))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private static List<JsonMappedRule> getJsonMappedRules(String filename) throws IOException {
        InputStream in = JsonRuleIngestionService.class.getResourceAsStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, UTF_8));
        char[] buffer = new char[1000000];
        br.read(buffer);
        in.close();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(valueOf(buffer), new TypeReference<List<JsonMappedRule>>() {});
    }

    private static List<AbstractRule> convertToRuleHeaders(List<JsonMappedRule> rules, RuleSource ruleSource) {
        return rules.stream()
                .map(rule -> {
                    RuleHeader ruleHeader = new RuleHeader(rule.getText(), ruleSource);
                    ruleHeader.addAll(convertToRuleSubheaders(rule.getSubRules()));
                    return ruleHeader;
                }
                )
                .collect(toList());
    }

    private static List<AbstractRule> convertToRuleSubheaders(List<JsonMappedRule> rules) {
        return rules.stream()
                .map(rule -> {
                    RuleSubheader ruleSubheader = new RuleSubheader(rule.getText());
                    ruleSubheader.addAll(convertToBaseRules(rule.getSubRules()));
                    return ruleSubheader;
                }
                )
                .collect(toList());
    }

    private static List<AbstractRule> convertToBaseRules(List<JsonMappedRule> rules) {
        return rules.stream()
                .map(rule -> new Rule(rule.getText()))
                .collect(toList());
    }
}
