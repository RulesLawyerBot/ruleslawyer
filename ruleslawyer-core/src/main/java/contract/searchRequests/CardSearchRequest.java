package contract.searchRequests;

import contract.cards.Card;
import contract.cards.FormatLegality;

import java.util.List;

import static contract.searchRequests.CardSearchRequestType.INCLUDE_ORACLE;
import static contract.searchRequests.CardSearchRequestType.MATCH_TITLE;

public class CardSearchRequest extends SearchRequest<Card> {

    private CardSearchRequestType cardSearchRequestType;
    private FormatLegality formatLegality;
    private boolean isFuzzy;

    public CardSearchRequest(List<String> keywords, FormatLegality formatLegality, Integer pageNumber, CardSearchRequestType cardSearchRequestType) {
        this.keywords = keywords;
        this.formatLegality = formatLegality;
        this.cardSearchRequestType = cardSearchRequestType;
        this.isFuzzy = false;
        this.pageNumber = pageNumber;
    }

    public CardSearchRequestType getCardSearchRequestType() {
        return cardSearchRequestType;
    }

    public FormatLegality getFormatLegality() {
        return formatLegality;
    }

    public boolean isFuzzy() {
        return this.isFuzzy;
    }

    public CardSearchRequest makeFuzzy() {
        this.isFuzzy = true;
        return this;
    }
}
