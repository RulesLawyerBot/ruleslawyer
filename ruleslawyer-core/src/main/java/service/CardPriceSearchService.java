package service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.*;

public class CardPriceSearchService {

    public CardPriceSearchService() {

    }

    public List<CardPriceReturnObject> getPrices(List<List<String>> scryfallURLs) {
        return scryfallURLs.stream()
                .map(elem ->
                        {
                            try {
                                return new CardPriceReturnObject(elem.get(0), makeStringForPriceMap(getPrice(elem.get(1))));
                            } catch (IOException e) {
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(toList());
    }

    public Map<String, String> getPrice(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(responseStream, Map.class);
        Map<String, String> prices = (Map<String, String>) map.get("prices");
        return prices;
    }

    private String makeStringForPriceMap(Map<String, String> inputMap) {
        return inputMap.entrySet().stream()
                .map(set -> {
                    String currency = set.getKey();
                    String price = set.getValue();
                    if (price == null) {
                        return "";
                    }
                    if (currency.equals("usd") || (currency.equals("usd_foil")) && !inputMap.containsKey("usd")) {
                        return "$" + price;
                    } else if (currency.equals("eur") || (currency.equals("eur_foil")) && !inputMap.containsKey("eur")) {
                        return "€" + price;
                    } else if (currency.equals("tix")) {
                        return price + "TIX";
                    }
                    return "";
                })
                .collect(joining(" "))
                .trim();
    }
}
