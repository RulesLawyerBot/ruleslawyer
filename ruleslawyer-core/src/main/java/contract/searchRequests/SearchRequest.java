package contract.searchRequests;

import contract.Searchable;

import java.util.List;

public abstract class SearchRequest<T extends Searchable> {
    protected List<String> keywords;
    protected Integer pageNumber;

    public List<String> getKeywords() {
        return keywords;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

}
