package contract;

public class RuleSearchResult {

    private String result;
    private boolean hasMore;

    public RuleSearchResult(String result, boolean hasMore) {
        this.result = result;
        this.hasMore = hasMore;
    }

    public String getResult() {
        return result;
    }

    public boolean hasMore() {
        return hasMore;
    }
}
