package contract;

import exception.NotYetImplementedException;

public class SearchResult<T extends Searchable> implements Comparable {

    private T entry;
    private Integer relevancy;

    public SearchResult(T entry, Integer relevancy) {
        this.entry = entry;
        this.relevancy = relevancy;
    }

    public T getEntry() {
        return entry;
    }

    public Integer getRelevancy() {
        return relevancy;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof SearchResult)) {
            throw new NotYetImplementedException();
        }
        int relevancyDiff = this.relevancy - ((SearchResult) o).relevancy;
        return relevancyDiff == 0 ?
                this.entry.compareTo(((SearchResult) o).getEntry()) :
                relevancyDiff;
    }
}
