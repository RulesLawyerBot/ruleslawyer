package search.contract.request;

import contract.cards.Card;
import contract.cards.FormatLegality;
import contract.searchRequests.CardSearchRequest;
import service.interaction_pagination.pagination_enum.CardPage;

import java.util.List;
import java.util.Optional;

import static service.interaction_pagination.pagination_enum.CardPage.ORACLE;

public class DiscordCardSearchRequest extends CardSearchRequest implements DiscordSearchRequestInterface<Card> {

    private String requester;
    private String channelName;
    private CardPage cardPage;

    public DiscordCardSearchRequest(
            List<String> keywords,
            FormatLegality formatLegality,
            String requester,
            String channelName,
            CardPage cardPage) {
        super(keywords, formatLegality);
        this.requester = requester;
        this.channelName = channelName;
        this.cardPage = cardPage;
    }

    @Override
    public String getRequester() {
        return requester;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    public CardPage getCardPage() {
        return cardPage;
    }
}
