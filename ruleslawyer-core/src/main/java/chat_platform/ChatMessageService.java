package chat_platform;

import contract.RequestSource;
import contract.RuleSearchResult;
import contract.RuleSource;
import contract.SearchResult;
import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import contract.searchRequests.builder.RuleSearchRequestBuilder;
import repository.SearchRepository;

import java.util.List;

import static contract.searchRequests.builder.RuleSearchRequestBuilder.aRuleSearchRequest;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ChatMessageService {

    private RequestSource requestSource;
    private SearchRepository<AbstractRule> searchRepository;
    private RulePrinterService rulePrinterService;

    public static final String HASMORE_MESSSAGE = "There are more results! To get them, filter by putting more words in the query. Pagination is currently in development (you have any idea how hard that is?)";

    public ChatMessageService(RequestSource requestSource, SearchRepository<AbstractRule> searchRepository) {
        this.requestSource = requestSource;
        this.searchRepository = searchRepository;
        this.rulePrinterService = new RulePrinterService(requestSource);
    }

    public List<String> processMessage(String message) {
        String query = getQuery(message).trim();
        if (query.length() == 0) {
            return emptyList();
        }

        RuleSearchRequest searchRequest = getSearchRequest(query);

        List<SearchResult<AbstractRule>> searchResults = searchRepository.getSearchResult(searchRequest);

        RuleSearchResult result = rulePrinterService.getOutputFromRawResults(searchResults);
        if (result.hasMore()) {
            return asList(HASMORE_MESSSAGE, result.getResult());
        } else {
            return singletonList(result.getResult());
        }
    }

    private String getQuery(String message) {
        int indexLeft = message.indexOf("{{");
        int indexRight = message.indexOf("}}");
        if (indexLeft == -1 || indexRight == -1 || indexRight < indexLeft)
            return "";
        return message.substring(indexLeft+2, indexRight);
    }

    private RuleSearchRequest getSearchRequest(String query) {
        RuleSearchRequestBuilder ruleSearchRequest = aRuleSearchRequest();

        List<String> commands = asList(query.split("\\|"));
        String keywords = commands.get(0);
        commands = commands.subList(1, commands.size());

        if(keywords.startsWith("\"") && keywords.endsWith("\"")) {
            ruleSearchRequest.setKeywords(singletonList(keywords.substring(1, keywords.length()-1)));
        }
        else {
            ruleSearchRequest.setKeywords(asList(keywords.split(" ")));
        }

        commands.forEach(
                command -> {
                    if(command.startsWith("p")) {
                        try {
                            ruleSearchRequest.setPageNumber(parseInt(command.substring(1)));
                        } catch (NumberFormatException ignored) {
                        }
                    } else {
                        try {
                            ruleSearchRequest.setRuleSource(RuleSource.valueOf(command.toUpperCase()));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
        );

        return ruleSearchRequest.build();
    }
}
