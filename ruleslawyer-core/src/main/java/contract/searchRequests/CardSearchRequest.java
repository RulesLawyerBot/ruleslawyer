package contract.searchRequests;

import contract.cards.Card;
import contract.cards.GameFormat;

import java.util.List;

public class CardSearchRequest extends SearchRequest<Card> {

    private CardSearchRequestType cardSearchRequestType;
    private GameFormat formats;
    private boolean isFuzzy;

    public CardSearchRequest(List<String> keywords, GameFormat formats, Integer pageNumber, CardSearchRequestType cardSearchRequestType) {
        this.keywords = keywords;
        this.formats = formats;
        this.cardSearchRequestType = cardSearchRequestType;
        this.isFuzzy = false;
        this.pageNumber = pageNumber;
    }

    public CardSearchRequestType getCardSearchRequestType() {
        return cardSearchRequestType;
    }

    public GameFormat getFormats() {
        return formats;
    }

    public boolean isFuzzy() {
        return this.isFuzzy;
    }

    public CardSearchRequest makeFuzzy() {
        this.isFuzzy = true;
        return this;
    }
}
