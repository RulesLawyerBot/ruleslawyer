package service;

import contract.cards.Card;
import contract.searchRequests.CardSearchRequest;
import contract.searchResults.SearchResult;
import repository.SearchRepository;

import java.util.Collections;
import java.util.List;

import static ingestion.card.JsonCardIngestionService.getCards;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class RawCardSearchService {

    SearchRepository<Card> repository;

    public RawCardSearchService(List<Card> cards) {
        repository = new SearchRepository<>(cards);
    }

    public List<Card> getCardsWithOracleFallback(CardSearchRequest request) {
        if (request.getKeywords().size() < 1 || request.getKeywords().stream().mapToInt(String::length).sum() < 1) {
            return emptyList();
        }
        List<SearchResult<Card>> cardSearchResults = repository.getSearchResult(request);
        return cardSearchResults.stream()
                .map(SearchResult::getEntry)
                .collect(toList());
    }
}
