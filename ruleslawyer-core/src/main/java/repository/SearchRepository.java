package repository;

import contract.searchResults.SearchResult;
import contract.Searchable;
import contract.searchRequests.SearchRequest;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SearchRepository<T extends Searchable> {

    private List<T> searchSpace;

    public SearchRepository(List<T> searchSpace) {
        this.searchSpace = searchSpace;
    }

    public List<SearchResult<T>> getSearchResult(SearchRequest<T> searchRequest) {
        return searchSpace.stream()
                 .map(searchObject -> searchObject.searchForKeywords(searchRequest))
                 .flatMap(Collection::stream)
                 .map(elem -> (T)elem)
                 .map(result -> new SearchResult<>(result, result.getRelevancy(searchRequest.getKeywords())))
                 .sorted()
                 .collect(toList());
    }
}
