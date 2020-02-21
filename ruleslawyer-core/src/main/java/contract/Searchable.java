package contract;

import contract.searchRequests.SearchRequest;

import java.util.List;

public interface Searchable extends Comparable {

    List<? extends Searchable> searchForKeywords(SearchRequest searchRequest);
    List<? extends Searchable> searchForKeywords(List<String> keywords);
    Integer getRelevancy(List<String> keywords);
}