package contract.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import contract.Searchable;
import contract.searchRequests.CardSearchRequest;
import contract.searchRequests.SearchRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Card implements Searchable {

    private String cardName;
    private String manaCost;
    private String typeLine;
    private String oracleText;
    private List<String> rulings;
    private List<String> searchSpace;
    //TODO legality

    public Card(
            @JsonProperty("cardName") String cardName,
            @JsonProperty("manaCost") String manaCost,
            @JsonProperty("typeLine") String typeLine,
            @JsonProperty("oracleText") String oracleText,
            @JsonProperty("rulings") List<String> rulings,
            @JsonProperty("sets") List<String> sets
    ) {
        this.cardName = cardName;
        this.manaCost = manaCost;
        this.typeLine = typeLine;
        this.oracleText = oracleText;
        this.rulings = rulings;
        this.searchSpace = new ArrayList<>();
        searchSpace.addAll(asList(cardName.toLowerCase(), typeLine.toLowerCase(), oracleText.toLowerCase()));
        searchSpace.addAll(rulings.stream().map(String::toLowerCase).collect(Collectors.toList()));
    }

    public String getCardName() {
        return cardName;
    }

    public String getManaCost() {
        return manaCost;
    }

    public String getTypeLine() {
        return typeLine;
    }

    public String getOracleText() {
        return oracleText;
    }

    public List<String> getRulings() {
        return rulings;
    }

    public List<String> getSearchSpace() {
        return searchSpace;
    }

    @Override
    public List<? extends Searchable> searchForKeywords(SearchRequest searchRequest) {
        List<String> keywords = ((CardSearchRequest)searchRequest).getKeywords();
        return searchForKeywords(keywords);
    }

    @Override
    public List<? extends Searchable> searchForKeywords(List<String> keywords) {
        return keywords.stream()
                .map(String::toLowerCase)
                .allMatch(
                        keyword -> searchSpace.stream()
                        .anyMatch(searchElement -> searchElement.contains(keyword))
                )
                ? singletonList(this)
                : emptyList();
    }

    @Override
    public Integer getRelevancy(List<String> keywords) {
        return 0; //TODO I have no fucking idea tbh
    }

    @Override
    public int compareTo(Object o) {
        return cardName.compareToIgnoreCase(((Card)o).cardName);
    }
}
