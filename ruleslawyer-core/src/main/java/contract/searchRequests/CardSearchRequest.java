package contract.searchRequests;

import contract.cards.Card;
import contract.cards.FormatLegality;

import java.util.List;

public class CardSearchRequest extends SearchRequest<Card> {

    private CardSearchRequestType cardSearchRequestType;
    private FormatLegality formatLegality;

    public CardSearchRequest(List<String> keywords, CardSearchRequestType cardSearchRequestType, FormatLegality formatLegality) {
        this.keywords = keywords;
        this.cardSearchRequestType = cardSearchRequestType;
        this.formatLegality = formatLegality;
    }

    public CardSearchRequestType getCardSearchRequestType() {
        return cardSearchRequestType;
    }

    public FormatLegality getFormatLegality() {
        return formatLegality;
    }
}
