package contract.cards;

import contract.Searchable;
import contract.searchRequests.SearchRequest;

import java.util.List;

//TODO
public class Card implements Searchable {
    @Override
    public List<? extends Searchable> searchForKeywords(SearchRequest searchRequest) {
        return null;
    }

    @Override
    public List<? extends Searchable> searchForKeywords(List<String> keywords) {
        return null;
    }

    @Override
    public Integer getRelevancy(List<String> keywords) {
        return null;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
