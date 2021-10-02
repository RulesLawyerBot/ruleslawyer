package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import contract.cards.CardSet;
import contract.cards.CardSetType;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static contract.cards.CardSetType.FOIL_ONLY_SET;
import static contract.cards.CardSetType.MTGO_SET;
import static java.util.stream.Collectors.*;

public class CardPriceSearchService {

    public CardPriceSearchService() {

    }

    public List<CardPriceReturnObject> getPrices(List<CardSet> cardSets) {
        return cardSets.stream()
                .map(elem ->
                        {
                            try {
                                return new CardPriceReturnObject(
                                        elem.getSetName(),
                                        makeStringForPriceMap(getPrice(elem.getSetUrl()), elem.getCardSetType())
                                );
                            } catch (IOException e) {
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(toList());
    }

    public Map<String, String> getPrice(String urlString) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestProperty("accept", "application/json");
        Map<String, Object> map = new ObjectMapper().readValue(connection.getInputStream(), Map.class);
        return (Map<String, String>) map.get("prices");
    }

    private String makeStringForPriceMap(Map<String, String> inputMap, CardSetType cardSetType) {
        return inputMap.entrySet().stream()
                .map(set -> {
                    String currency = set.getKey();
                    String price = set.getValue();
                    if (price == null) {
                        return "";
                    }
                    if (currency.equals("usd") || (currency.equals("usd_foil")) && (!inputMap.containsKey("usd") || inputMap.get("usd") == null)) {
                        return "$" + price;
                    } else if (currency.equals("eur") || (currency.equals("eur_foil")) && (!inputMap.containsKey("eur") || inputMap.get("eur") == null)) {
                        return "€" + price;
                    } else if (currency.equals("tix")) {
                        return price + " TIX";
                    }
                    return "";
                })
                .filter(string -> string.length() > 2)
                .collect(joining("\n"))
                .trim();
    }
}
