package ingestion.rule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import contract.rules.enums.RuleSource;
import contract.rules.AbstractRule;
import contract.rules.Rule;
import contract.rules.RuleHeader;
import contract.rules.RuleSubheader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static contract.rules.enums.RuleSource.*;
import static ingestion.rule.CitationInitService.setOutboundCitations;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class JsonRuleIngestionService {

    public static List<AbstractRule> getRawRulesData() {
        try {
            System.out.println("Loading rules...");
            List<AbstractRule> rules = new ArrayList<>();
            rules.addAll(getRawRulesData("/CR-parsed.json", CR));
            rules.addAll(getRawRulesData("/CRG-parsed.json", CRG));
            rules.addAll(getRawRulesData("/JAR-parsed.json", JAR));
            rules.addAll(getRawRulesData("/IPG-parsed.json", IPG));
            rules.addAll(getRawRulesData("/MTR-parsed.json", MTR));
            // rules.addAll(getFlattenedRules("/oath-parsed.json", OATH)); TODO bring this back after fixing the parser
            System.out.println("Setting citations...");
            setOutboundCitations(rules);
            return rules;
        } catch (IOException ignored) {
            System.exit(-1);
        }
        return emptyList();
    }

    public static List<AbstractRule> getRawDigitalRulesData() {
        try {
            System.out.println("Loading digital rules...");
            List<AbstractRule> rules = new ArrayList<>();
            rules.addAll(getRawRulesData("/DIPG-parsed.json", DIPG));
            rules.addAll(getRawRulesData("/DMTR-parsed.json", DMTR));
            return rules;
        } catch (IOException ignored) {
            System.exit(-1);
        }
        return emptyList();
    }

    private static List<AbstractRule> getRawRulesData(String filename, RuleSource ruleSource) throws IOException {
        List<JsonMappedRule> rawRules = getJsonMappedRules(filename);
        return rawRules.stream()
                .map(rule -> convertToRuleHeaders(singletonList(rule), ruleSource))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private static List<AbstractRule> getFlattenedRules(String filename, RuleSource ruleSource) throws IOException {
        List<JsonMappedRule> rawRules = getJsonMappedRules(filename);
        return rawRules.stream()
                .map(rule ->
                        Stream.of(getCRRuleHeader(rule, ruleSource), convertToRuleHeaders(rule.getSubRules(), ruleSource))
                                .flatMap(Collection::stream)
                                .collect(toList())
                )
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private static List<JsonMappedRule> getJsonMappedRules(String filename) throws IOException {
        InputStream in = JsonRuleIngestionService.class.getResourceAsStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("Windows-1252")));
        char[] buffer = new char[1000000];
        br.read(buffer);
        in.close();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(valueOf(buffer), new TypeReference<List<JsonMappedRule>>() {});
    }

    private static List<AbstractRule> getCRRuleHeader(JsonMappedRule rule, RuleSource ruleSource) {
        return singletonList(new RuleHeader(rule.getText(), ruleSource, rule.getCitations()));
    }

    private static List<AbstractRule> convertToRuleHeaders(List<JsonMappedRule> rules, RuleSource ruleSource) {
        return rules.stream()
                .map(rule -> {
                    RuleHeader ruleHeader = new RuleHeader(rule.getText(), ruleSource, rule.getCitations());
                    ruleHeader.addAll(convertToRuleSubheaders(rule.getSubRules()));
                    return ruleHeader;
                }
                )
                .collect(toList());
    }

    private static List<AbstractRule> convertToRuleSubheaders(List<JsonMappedRule> rules) {
        return rules.stream()
                .map(rule -> {
                    RuleSubheader ruleSubheader = new RuleSubheader(rule.getText(), rule.getCitations());
                    ruleSubheader.addAll(convertToBaseRules(rule.getSubRules()));
                    return ruleSubheader;
                }
                )
                .collect(toList());
    }

    private static List<AbstractRule> convertToBaseRules(List<JsonMappedRule> rules) {
        return rules.stream()
                .map(rule -> new Rule(rule.getText(), rule.getCitations()))
                .collect(toList());
    }
}
