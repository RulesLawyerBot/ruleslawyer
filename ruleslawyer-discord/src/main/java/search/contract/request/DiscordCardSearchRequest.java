package search.contract.request;

import contract.cards.Card;
import contract.cards.FormatLegality;
import contract.searchRequests.CardSearchRequest;
import search.interaction_pagination.pagination_enum.CardDataReturnType;
import search.interaction_pagination.pagination_enum.CardPageDirection;

import java.util.List;

import static search.interaction_pagination.pagination_enum.CardPageDirection.PREVIOUS_CARD;

public class DiscordCardSearchRequest extends CardSearchRequest implements DiscordSearchRequestInterface<Card> {

    private String requester;
    private CardDataReturnType cardDataReturnType;

    public DiscordCardSearchRequest(
            List<String> keywords,
            FormatLegality formatLegality,
            String requester,
            CardDataReturnType cardDataReturnType,
            Integer pageNumber
    ) {
        super(keywords, formatLegality, pageNumber);
        this.requester = requester;
        this.cardDataReturnType = cardDataReturnType;
    }

    @Override
    public String getRequester() {
        return requester;
    }

    public CardDataReturnType getCardDataReturnType() {
        return cardDataReturnType;
    }

    public void paginateSearchRequest(CardPageDirection cardPageDirection) {
        if (cardPageDirection == PREVIOUS_CARD) {
            pageNumber--;
        } else {
            pageNumber++;
        }
    }

    public void setCardDataReturnType(CardDataReturnType cardDataReturnType) {
        this.cardDataReturnType = cardDataReturnType;
    }
}
