package contract;

import contract.searchRequests.SearchRequest;

import java.util.List;
import java.util.Optional;

public interface Searchable extends Comparable {

    List<? extends Searchable> searchForKeywords(SearchRequest searchRequest);

    List<? extends Searchable> searchForKeywords(List<String> keywords);

    List<? extends Searchable> fuzzySearchForKeywords(SearchRequest searchRequest, Integer fuzzyDistance);

    Integer getRelevancy(List<String> keywords);

    Integer getFuzzyRelevancy(List<String> keywords, Integer fuzzyDistance);

    Optional<?> findByIndex(Integer index);
}