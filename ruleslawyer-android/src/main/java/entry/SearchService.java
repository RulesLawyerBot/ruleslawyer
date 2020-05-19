package entry;

import contract.SearchResult;
import contract.cards.Card;
import contract.rules.AbstractRule;
import contract.searchRequests.CardSearchRequest;
import contract.searchRequests.RuleSearchRequest;
import ingestion.card.JsonCardIngestionService;
import ingestion.rule.JsonRuleIngestionService;
import repository.SearchRepository;

import java.util.List;

public class SearchService {
    private static SearchRepository<AbstractRule> ruleRepository;
    private static SearchRepository<Card> cardRepository;

    public static void setUp() {
        List<AbstractRule> rules = JsonRuleIngestionService.getRules();
        ruleRepository = new SearchRepository<>(rules);

        List<Card> cards = JsonCardIngestionService.getCards();
        cardRepository = new SearchRepository<>(cards);
    }

    public static List<SearchResult<AbstractRule>> getRuleSearchResults(RuleSearchRequest ruleSearchRequest) {

        return ruleRepository.getSearchResult(ruleSearchRequest);
    }

    public static List<SearchResult<Card>> getCardSearchResults(CardSearchRequest cardSearchRequest) {
        return cardRepository.getSearchResult(cardSearchRequest);
    }
}
