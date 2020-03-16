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

    private static final String HASMORE_MESSSAGE = "There are more results! To get them, filter by putting more words in the query. Pagination is currently in development (you have any idea how hard that is?)";
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
            if (query.equals("help")) {
                return singletonList(helpMessageService.getHelpFile());
            }
            else {
                return singletonList(helpMessageService.getHelpFile(query.substring(5)));
            }
        }

        RuleSearchRequest searchRequest = getSearchRequest(query);

        List<SearchResult<AbstractRule>> searchResults = searchRepository.getSearchResult(searchRequest);

        RuleSearchResult result = rulePrinterService.getOutputFromRawResults(searchResults, searchRequest);
        if (result.hasMore()) {
            return asList(result.getQueryInfo() + "\n" + HASMORE_MESSSAGE, result.getResult());
        } else {
            if (result.getResult() == null || result.getResult().length() == 0)
                return singletonList(NO_RESULTS_MESSAGE);
            return asList(result.getQueryInfo(), result.getResult());
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
}
