package contract.cards;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardSet {

    private CardSetType cardSetType;
    private String setUrl;
    private String setName;

    public CardSet(
            @JsonProperty("cardSetType") CardSetType cardSetType,
            @JsonProperty("setUrl") String setUrl,
            @JsonProperty("setName") String setName
    ) {
        this.cardSetType = cardSetType;
        this.setUrl = setUrl;
        this.setName = setName;
    }

    public CardSetType getCardSetType() {
        return cardSetType;
    }

    public String getSetUrl() {
        return setUrl;
    }

    public String getSetName() {
        return setName;
    }
}
