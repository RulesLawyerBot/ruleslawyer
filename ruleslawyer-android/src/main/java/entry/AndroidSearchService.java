package entry;

import contract.searchResults.SearchResult;
import contract.cards.Card;
import contract.rules.AbstractRule;
import contract.searchRequests.CardSearchRequest;
import contract.searchRequests.RuleSearchRequest;
import ingestion.card.JsonCardIngestionService;
import ingestion.rule.JsonRuleIngestionService;
import repository.SearchRepository;

import java.util.List;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static contract.rules.enums.RuleRequestCategory.ANY_RULE_TYPE;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static contract.searchRequests.CardSearchRequestType.INCLUDE_ORACLE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class AndroidSearchService {
    private static JsonRuleIngestionService jsonRuleIngestionService = new JsonRuleIngestionService();
    private static SearchRepository<AbstractRule> ruleRepository;
    private static SearchRepository<Card> cardRepository;

    public static void setUp() {
        List<AbstractRule> rules = jsonRuleIngestionService.getRawRulesData();
        ruleRepository = new SearchRepository<>(rules);

        List<Card> cards = JsonCardIngestionService.getCards();
        cardRepository = new SearchRepository<>(cards);
    }

    public static List<AbstractRule> getRuleSearchResults(String input) {
        RuleSearchRequest ruleSearchRequest = new RuleSearchRequest(asList(input.split(" ")), ANY_DOCUMENT, 0, ANY_RULE_TYPE);
        return ruleRepository.getSearchResult(ruleSearchRequest)
                .stream()
                .map(SearchResult::getEntry)
                .collect(toList());
    }

    public static List<Card> getCardSearchResults(String input) {
        CardSearchRequest cardSearchRequest = new CardSearchRequest(asList(input.split(" ")), INCLUDE_ORACLE, ANY_FORMAT);
        return cardRepository.getSearchResult(cardSearchRequest)
                .stream()
                .map(SearchResult::getEntry)
                .collect(toList());
    }
}
