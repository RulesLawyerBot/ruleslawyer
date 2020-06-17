package chat_platform;

import contract.RequestSource;
import contract.RuleSearchResult;
import contract.SearchResult;
import contract.rules.AbstractRule;
import contract.rules.Rule;
import contract.rules.RuleHeader;
import contract.rules.RuleSubheader;
import contract.searchRequests.RuleSearchRequest;
import exception.NotYetImplementedException;

import java.util.ArrayList;
import java.util.List;

import static contract.RequestSource.DISCORD;
import static contract.RequestSource.SLACK;
import static contract.RuleSource.ANY;
import static java.lang.String.join;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class RulePrinterService {

    private RequestSource requestSource;
    private String boldDelimiter;
    private String codeLineDelimiter;
    private Integer maxMessageLength;

    private static final Integer DISCORD_MAX_MESSAGE_LENGTH = 2000;
    private static final Integer SLACK_MAX_MESSAGE_LENGTH = 10000;

    public RulePrinterService(RequestSource requestSource) {
        this.requestSource = requestSource;
        if (requestSource == DISCORD) {
            boldDelimiter = "**";
            codeLineDelimiter = "```";
            maxMessageLength = DISCORD_MAX_MESSAGE_LENGTH;
        }
        if (requestSource == SLACK) {
            boldDelimiter = "*";
            codeLineDelimiter = "```";
            maxMessageLength = SLACK_MAX_MESSAGE_LENGTH;
        }
    }

    //TODO testcase
    public RuleSearchResult getOutputFromRawResults(List<SearchResult<AbstractRule>> searchResults, RuleSearchRequest ruleSearchRequest) {
        Integer page = ruleSearchRequest.getPageNumber();

        while (true) {
            List<AbstractRule> theseResults = getNextPageOfResults(searchResults);

            if (searchResults.isEmpty()) {
                return new RuleSearchResult(printRules(theseResults), false);
            }
            if (page == 0) {
                return new RuleSearchResult(printRules(theseResults), true);
            }
            page--;
        }
    }

    //modifies searchResults list
    private List<AbstractRule> getNextPageOfResults(List<SearchResult<AbstractRule>> searchResults) {
        List<AbstractRule> output = new ArrayList<>();

        while (true) {
            String outputString = printRules(output);
            if (outputString.length() > maxMessageLength) {
                if (output.size() == 1) {
                    List<SearchResult<AbstractRule>> subResults = output.get(0).getSubRules()
                            .stream()
                            .map(elem -> new SearchResult<>(elem, 0))
                            .collect(toList());
                    searchResults.remove(0);
                    searchResults.addAll(0, subResults);
                    return getNextPageOfResults(searchResults);
                } else {
                    output.remove(output.size()-1);
                    searchResults.subList(0, output.size()).clear();
                    return output;
                }
            }
            if (output.size() == searchResults.size()) {
                searchResults.clear();
                return output;
            } else {
                output.add(searchResults.get(output.size()).getEntry());
            }
        }

    }

    public String printRules(List<AbstractRule> inputRules) {
        List<AbstractRule> rules = inputRules.stream().sorted().collect(toList());
        StringBuilder output = new StringBuilder();
        RuleHeader lastHeader = null;
        RuleSubheader lastSubheader = null;
        for (int i=0; i<rules.size(); i++) {
            AbstractRule elem = rules.get(i);
            if (elem.getClass() == RuleHeader.class) {
                lastHeader = (RuleHeader) elem;
                output.append(printRule(elem));
            }
            else if (elem.getClass() == RuleSubheader.class) {
                RuleHeader thisHeader = ((RuleSubheader)elem).getHeader();
                if (lastHeader != thisHeader) {
                    output.append(printBaseRuleHeader(thisHeader));
                    lastHeader = thisHeader;
                }
                output.append(printRule(elem));
            }
            else if (elem.getClass() == Rule.class) {
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
        return "\n> " + boldDelimiter + ruleHeader.getRuleSource() + " " + ruleHeader.getText() + boldDelimiter + "\n";
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
        return ruleSubheader.getText() + "\n";
    }

    public String printRule(Rule rule) {
        return codeLineDelimiter + rule.getText() + codeLineDelimiter;
    }

    public String printRequestToQuery(RuleSearchRequest ruleSearchRequest) {
        String keywordsString = join("|", ruleSearchRequest.getKeywords());
        String pageString = "p" + ruleSearchRequest.getPageNumber();
        if (ruleSearchRequest.getRuleSource() == ANY) {
            return "{{" + keywordsString + "|" + pageString + "}}";
        }
        return "{{" + keywordsString + "|" + ruleSearchRequest.getRuleSource().toString() + "|" + pageString + "}}";
    }
}
