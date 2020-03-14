package contract;

public class RuleSearchResult {

    private String queryInfo;
    private String result;
    private boolean hasMore;

    public RuleSearchResult(String queryInfo, String result, boolean hasMore) {
        this.queryInfo = queryInfo;
        this.result = result;
        this.hasMore = hasMore;
    }

    public String getQueryInfo() {
        return queryInfo;
    }

    public String getResult() {
        return result;
    }

    public boolean hasMore() {
        return hasMore;
    }
}
