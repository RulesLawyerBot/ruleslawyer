package service;

public class CardPriceReturnObject {

    private String cardSetName;
    private String cardPrices;

    public CardPriceReturnObject(String cardSetName, String cardPrices) {
        this.cardSetName = cardSetName;
        this.cardPrices = cardPrices;
    }

    public String getCardSetName() {
        return cardSetName;
    }

    public String getCardPrices() {
        return cardPrices;
    }
}
