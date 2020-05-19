package contract.searchRequests;

import contract.cards.Card;

import java.util.List;

public class CardSearchRequest extends SearchRequest<Card> {

    public CardSearchRequest(List<String> keywords) {
        this.keywords = keywords;
    }

}
