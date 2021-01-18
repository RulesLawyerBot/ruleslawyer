package chat_platform;

import contract.cards.Card;
import contract.cards.FormatLegality;
import contract.searchRequests.CardSearchRequest;
import contract.searchResults.SearchResult;
import repository.SearchRepository;

import java.util.List;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static contract.searchRequests.CardSearchRequestType.TITLE_ONLY;
import static java.util.Arrays.asList;

public class ScryfallHelperService {
    private SearchRepository<Card> cardSearchRepository;

    public ScryfallHelperService(List<Card> searchSpace) {
        cardSearchRepository = new SearchRepository<>(searchSpace);
    }

    public String getCardName(String searchQuery, String channelName) {
        FormatLegality formatLegality = getFormat(channelName);
        CardSearchRequest cardSearchRequest = new CardSearchRequest(asList(searchQuery.split(" ")), TITLE_ONLY, formatLegality);
        List<SearchResult<Card>> searchResults = cardSearchRepository.getSearchResult(cardSearchRequest);
        return searchResults.size() < 2 ? null : searchResults.get(0).getEntry().getCardName();
    }

    private FormatLegality getFormat(String channelName) {
        try {
            return FormatLegality.valueOf(channelName);
        } catch (IllegalArgumentException ignored) {
            return ANY_FORMAT;
        }
    }
}
