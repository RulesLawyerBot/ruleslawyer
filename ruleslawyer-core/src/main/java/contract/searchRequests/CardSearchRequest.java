package contract.searchRequests;

import contract.cards.Card;
import contract.cards.FormatLegality;

import java.util.List;

import static contract.searchRequests.CardSearchRequestType.INCLUDE_ORACLE;
import static contract.searchRequests.CardSearchRequestType.TITLE_ONLY;

public class CardSearchRequest extends SearchRequest<Card> {

    private CardSearchRequestType cardSearchRequestType;
    private FormatLegality formatLegality;
    private boolean isFuzzy;

    public CardSearchRequest(List<String> keywords, FormatLegality formatLegality, Integer pageNumber) {
        this.keywords = keywords;
        this.formatLegality = formatLegality;
        this.cardSearchRequestType = TITLE_ONLY;
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

    public CardSearchRequest includeOracle() {
        this.cardSearchRequestType = INCLUDE_ORACLE;
        return this;
    }
}
