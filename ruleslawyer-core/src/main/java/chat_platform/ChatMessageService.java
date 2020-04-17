package chat_platform;

import contract.RequestSource;
import contract.RuleSearchResult;
import contract.RuleSource;
import contract.SearchResult;
import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import contract.searchRequests.SearchRequest;
import contract.searchRequests.builder.RuleSearchRequestBuilder;
import repository.SearchRepository;

import java.util.Arrays;
import java.util.List;

import static contract.searchRequests.builder.RuleSearchRequestBuilder.aRuleSearchRequest;
import static contract.searchRequests.builder.RuleSearchRequestBuilder.fromRuleSearchRequest;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class ChatMessageService {

    private RequestSource requestSource;
    private SearchRepository<AbstractRule> searchRepository;
    private RulePrinterService rulePrinterService;
    private HelpMessageService helpMessageService;

    private static final String NEXTPAGE_MESSAGE = "To get the next page of results, use ";
    private static final String NO_MORE_RESULTS_MESSAGE = "This is the final page of results.";
    private static final String NO_RESULTS_MESSAGE = "No results found :( If you believe this to be an error, please let me know at {{help|about}}. Otherwise, make sure you spelled everything correctly.";

    public ChatMessageService(RequestSource requestSource, SearchRepository<AbstractRule> searchRepository) {
        this.requestSource = requestSource;
        this.searchRepository = searchRepository;
        this.rulePrinterService = new RulePrinterService(requestSource);
        this.helpMessageService = new HelpMessageService();
    }

    public List<String> processMessage(String message) {
        String query = getQuery(message).trim();
        if (query.length() == 0) {
            return emptyList();
        }

        if (query.startsWith("help")) {
            return query.equals("help") ?
                    singletonList(helpMessageService.getHelpFile()) :
                    singletonList(helpMessageService.getHelpFile(query.substring(5)));
        }

        RuleSearchRequest searchRequest = getSearchRequest(query);

        List<SearchResult<AbstractRule>> searchResults = searchRepository.getSearchResult(searchRequest);

        String queryInfo = rulePrinterService.printRequest(searchRequest);
        RuleSearchResult searchResult = rulePrinterService. getOutputFromRawResults(searchResults, searchRequest);
        if (searchResult.hasMore()) {
            return asList(queryInfo + "\n" + NEXTPAGE_MESSAGE + getQueryStringForNextPage(searchRequest), searchResult.getResult());
        } else if (searchResult.getResult().length() == 0) {
            return singletonList(NO_RESULTS_MESSAGE);
        } else if (searchRequest.getPageNumber() != 0) {
            return asList(queryInfo + "\n" + NO_MORE_RESULTS_MESSAGE, searchResult.getResult());
        } else {
            return asList(queryInfo, searchResult.getResult());
        }
    }

    private String getQuery(String message) {
        int indexLeft = message.indexOf("{{");
        int indexRight = message.indexOf("}}");
        if (indexLeft == -1 || indexRight == -1 || indexRight < indexLeft)
            return "";
        return message.substring(indexLeft+2, indexRight).toLowerCase();
    }

    private RuleSearchRequest getSearchRequest(String query) {
        RuleSearchRequestBuilder ruleSearchRequest = aRuleSearchRequest();

        List<String> commands = asList(query.split("\\|"));

        commands.forEach(
                command -> {
                    if(command.startsWith("p")) {
                        try {
                            ruleSearchRequest.setPageNumber(parseInt(command.substring(1)));
                        } catch (NumberFormatException ignored) {
                            ruleSearchRequest.appendKeywords(getKeywordsFromSubquery(command));
                        }
                    } else {
                        try {
                            ruleSearchRequest.setRuleSource(RuleSource.valueOf(command.toUpperCase()));
                        } catch (IllegalArgumentException ignored) {
                            ruleSearchRequest.appendKeywords(getKeywordsFromSubquery(command));
                        }
                    }
                }
        );

        return ruleSearchRequest.build();
    }

    private List<String> getKeywordsFromSubquery(String subquery) {
        if (subquery.startsWith("\"") && subquery.endsWith("\"")) {
            return singletonList(subquery.substring(1, subquery.length()-1).toLowerCase());
        }
        else {
            return stream(subquery.split(" "))
                    .map(String::toLowerCase)
                    .collect(toList());
        }
    }

    private String getQueryStringForNextPage(RuleSearchRequest ruleSearchRequest) {
        RuleSearchRequest nextPage = fromRuleSearchRequest(ruleSearchRequest)
                .setPageNumber(ruleSearchRequest.getPageNumber()+1)
                .build();
        return rulePrinterService.printRequestToQuery(nextPage);
    }
}
