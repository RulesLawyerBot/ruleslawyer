package repository;

import contract.searchResults.SearchResult;
import contract.Searchable;
import contract.searchRequests.SearchRequest;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

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

    public List<SearchResult<T>> getFuzzySearchResult(SearchRequest<T> searchRequest, Integer fuzzyDistance) {
        return searchSpace.stream()
                .map(searchObject -> searchObject.fuzzySearchForKeywords(searchRequest, fuzzyDistance))
                .flatMap(Collection::stream)
                .map(elem -> (T)elem)
                .map(result -> new SearchResult<>(result, result.getFuzzyRelevancy(searchRequest.getKeywords(), fuzzyDistance)))
                .sorted()
                .collect(toList());
    }

    public T findByIndex(Integer index) throws NoSuchElementException {
        return searchSpace.stream()
                .map(searchObject -> searchObject.findByIndex(index))
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                .map(elem -> (T)elem)
                .findFirst()
                .get();
    }
}
