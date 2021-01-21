package search.contract;

import contract.RuleSource;
import contract.searchRequests.RuleSearchRequest;
import service.reaction_pagination.PageDirection;

import java.util.List;
import java.util.Objects;

public class DiscordSearchRequest extends RuleSearchRequest {

    private String requester;

    public DiscordSearchRequest(String requester, List<String> keywords, RuleSource ruleSource, Integer pageNumber, Boolean isDigitalRuleRequest) {
        super(keywords, ruleSource, pageNumber, isDigitalRuleRequest);
        this.requester = requester;
    }

    public String getRequester() {
        return requester;
    }

    public DiscordSearchRequest getNextPage(PageDirection pageDirection) {
        if (pageDirection == PageDirection.NEXT_PAGE) {
            this.pageNumber++;
        } else {
            this.pageNumber--;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscordSearchRequest)) return false;
        if (!super.equals(o)) return false;
        DiscordSearchRequest that = (DiscordSearchRequest) o;
        return requester.equals(that.requester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requester);
    }
}
