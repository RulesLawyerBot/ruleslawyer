package chat_platform;

import contract.RequestSource;
import contract.RuleSearchResult;
import contract.SearchResult;
import contract.rules.AbstractRule;
import contract.rules.Rule;
import contract.rules.RuleHeader;
import contract.rules.RuleSubheader;
import exception.NotYetImplementedException;

import java.util.ArrayList;
import java.util.List;

import static contract.RequestSource.DISCORD;
import static contract.RequestSource.SLACK;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class RulePrinterService {

    RequestSource requestSource;
    String boldDelimiter;
    Integer maxMessageLength;

    private static final Integer DISCORD_MAX_MESSAGE_LENGTH = 2000;
    private static final Integer SLACK_MAX_MESSAGE_LENGTH = 10000;

    public RulePrinterService(RequestSource requestSource) {
        this.requestSource = requestSource;
        if (requestSource == DISCORD) {
            boldDelimiter = "**";
            maxMessageLength = DISCORD_MAX_MESSAGE_LENGTH;
        }
        if (requestSource == SLACK) {
            boldDelimiter = "*";
            maxMessageLength = SLACK_MAX_MESSAGE_LENGTH;
        }
    }

    public RuleSearchResult getOutputFromRawResults(List<SearchResult<AbstractRule>> searchResults) {
        List<AbstractRule> returnedResults = new ArrayList<>();

        while (true) {
            String output = printRules(returnedResults);
            if (output.length() > maxMessageLength) {
                if (returnedResults.size() == 1) {
                    return new RuleSearchResult(output.substring(0, 2000), true); //TODO
                } else {
                    returnedResults.remove(returnedResults.size() - 1);
                    return new RuleSearchResult(printRules(returnedResults), true);
                }
            }
            if (returnedResults.size() == searchResults.size()) {
                return new RuleSearchResult(output, false);
            } else {
                returnedResults.add(searchResults.get(returnedResults.size()).getEntry());
            }
        }
    }

    public String printRules(List<AbstractRule> rules) {
        rules = rules.stream().sorted().collect(toList());
        StringBuilder output = new StringBuilder();
        RuleHeader lastHeader = null;
        RuleSubheader lastSubheader = null;
        for (int i=0; i<rules.size(); i++) {
            AbstractRule elem = rules.get(i);
            if (elem.getClass() == RuleHeader.class) {
                lastHeader = (RuleHeader) elem;
                output.append(printRule(elem));
                continue;
            }
            if (elem.getClass() == RuleSubheader.class) {
                RuleHeader thisHeader = ((RuleSubheader)elem).getHeader();
                if (lastHeader != thisHeader) {
                    output.append(printBaseRuleHeader(thisHeader));
                    lastHeader = thisHeader;
                }
                output.append(printRule(elem));
                continue;
            }
            if (elem.getClass() == Rule.class) {
                RuleHeader thisHeader = ((Rule)elem).getHeader();
                RuleSubheader thisSubheader = ((Rule)elem).getSubHeader();
                if (lastSubheader != thisSubheader) {
                    if (lastHeader != thisHeader) {
                        output.append(printBaseRuleHeader(thisHeader));
                        lastHeader = thisHeader;
                    }
                    output.append(printBaseRuleSubheader(thisSubheader));
                    lastSubheader = thisSubheader;
                }
                output.append(printRule(elem));
                continue;
            }
        }
        return output.toString().replace("\n\n", "\n");
    }

    public String printRule(AbstractRule rule) {
        if (rule.getClass() == RuleHeader.class)
            return printRule((RuleHeader)rule);
        if (rule.getClass() == RuleSubheader.class)
            return printRule((RuleSubheader)rule);
        if (rule.getClass() == Rule.class)
            return printRule((Rule)rule);
        throw new NotYetImplementedException();
    }

    public String printRule(RuleHeader ruleHeader) {
        return printBaseRuleHeader(ruleHeader) +
                ruleHeader.getSubRules().stream().map(
                        rule -> {
                            if (rule.getClass() == RuleSubheader.class) {
                                return printRule((RuleSubheader)rule);
                            } else if (rule.getClass() == Rule.class) {
                                return printRule((Rule)rule);
                            }
                            throw new NotYetImplementedException();
                        }
                )
                .collect(joining("\n"));
    }

    private String printBaseRuleHeader(RuleHeader ruleHeader) {
        return boldDelimiter + ruleHeader.getRuleSource() + " " + ruleHeader.getText() + boldDelimiter + "\n";
    }

    public String printRule(RuleSubheader ruleSubheader) {
        String baseText = printBaseRuleSubheader(ruleSubheader);
        if (ruleSubheader.getSubRules() != null && ruleSubheader.getSubRules().size() > 0)
            return baseText + "```" +
                    ruleSubheader.getSubRules()
                            .stream()
                            .map(AbstractRule::getText)
                            .collect(joining("\n")) + "```";
        return baseText;
    }

    private String printBaseRuleSubheader(RuleSubheader ruleSubheader) {
        return "> " + ruleSubheader.getText() + "\n";
    }

    public String printRule(Rule rule) {
        return "```" + rule.getText() + "```";
    }
}
