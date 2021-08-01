package search.contract.request;

import contract.cards.Card;
import contract.cards.FormatLegality;
import contract.searchRequests.CardSearchRequest;
import search.interaction_pagination.pagination_enum.CardPage;

import java.util.List;

public class DiscordCardSearchRequest extends CardSearchRequest implements DiscordSearchRequestInterface<Card> {

    private String requester;
    private CardPage cardPage;

    public DiscordCardSearchRequest(
            List<String> keywords,
            FormatLegality formatLegality,
            String requester,
            CardPage cardPage) {
        super(keywords, formatLegality);
        this.requester = requester;
        this.cardPage = cardPage;
    }

    @Override
    public String getRequester() {
        return requester;
    }

    public CardPage getCardPage() {
        return cardPage;
    }
}
