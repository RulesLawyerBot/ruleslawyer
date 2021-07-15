package search.contract;

import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import contract.searchRequests.RuleSearchRequest;
import service.interaction_pagination.PageDirection;

import java.util.List;
import java.util.Objects;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static contract.rules.enums.RuleRequestCategory.PAPER;
import static service.interaction_pagination.PageDirection.*;

public class DiscordSearchRequest extends RuleSearchRequest {

    private String requester;
    private String channelName;

    public DiscordSearchRequest(
            String requester,
            String channelName,
            List<String> keywords,
            RuleSource ruleSource,
            Integer pageNumber,
            RuleRequestCategory ruleRequestCategory
    ) {
        super(keywords, ruleSource, pageNumber, ruleRequestCategory);
        this.requester = requester;
        this.channelName = channelName;
    }

    public String getRequester() {
        return requester;
    }

    public String getChannelName() {
        return channelName;
    }

    public DiscordSearchRequest getNextPage(PageDirection pageDirection) {
        if (pageDirection == NEXT_PAGE) {
            this.pageNumber++;
        } else if (pageDirection == PREVIOUS_PAGE) {
            this.pageNumber--;
        } else if (pageDirection == TO_DIGITAL) {
            this.ruleRequestCategory = DIGITAL;
        } else if (pageDirection == TO_PAPER) {
            this.ruleRequestCategory = PAPER;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscordSearchRequest)) return false;
        if (!super.equals(o)) return false;
        DiscordSearchRequest that = (DiscordSearchRequest) o;
        return Objects.equals(requester, that.requester) &&
                Objects.equals(channelName, that.channelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requester, channelName);
    }
}
