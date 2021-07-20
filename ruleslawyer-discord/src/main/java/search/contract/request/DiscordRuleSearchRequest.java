package search.contract.request;

import contract.rules.AbstractRule;
import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import contract.searchRequests.RuleSearchRequest;
import service.interaction_pagination.pagination_enum.PageDirection;

import java.util.List;
import java.util.Objects;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static contract.rules.enums.RuleRequestCategory.PAPER;
import static service.interaction_pagination.pagination_enum.PageDirection.*;

public class DiscordRuleSearchRequest extends RuleSearchRequest implements DiscordSearchRequestInterface<AbstractRule> {

    private String requester;
    private String channelName;

    public DiscordRuleSearchRequest(
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

    public DiscordRuleSearchRequest getNextPage(PageDirection pageDirection) {
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
        if (!(o instanceof DiscordRuleSearchRequest)) return false;
        if (!super.equals(o)) return false;
        DiscordRuleSearchRequest that = (DiscordRuleSearchRequest) o;
        return Objects.equals(requester, that.requester) &&
                Objects.equals(channelName, that.channelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requester, channelName);
    }
}
