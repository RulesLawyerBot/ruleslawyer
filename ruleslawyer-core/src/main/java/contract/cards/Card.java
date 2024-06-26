package contract.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import contract.Searchable;
import contract.searchRequests.CardSearchRequest;
import contract.searchRequests.SearchRequest;
import exception.NotYetImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static contract.cards.CardSetType.*;
import static contract.cards.GameFormat.ANY_FORMAT;
import static contract.cards.LegalityStatus.LEGAL;
import static contract.cards.LegalityStatus.NOT_LEGAL;
import static contract.searchRequests.CardSearchRequestType.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Card implements Searchable {

    private String cardName;
    private String manaCost;
    private String typeLine;
    private String oracleText;
    private List<String> rulings;
    private List<CardSet> sets;
    private Map<GameFormat, LegalityStatus> formatLegalities;
    private Integer edhrecRank;
    private List<String> image_urls;

    public Card(
            @JsonProperty("cardName") String cardName,
            @JsonProperty("manaCost") String manaCost,
            @JsonProperty("typeLine") String typeLine,
            @JsonProperty("oracleText") String oracleText,
            @JsonProperty("rulings") List<String> rulings,
            @JsonProperty("sets") List<CardSet> sets,
            @JsonProperty("legalities") Map<String, String> formatLegalities,
            @JsonProperty("edhrec_rank") Integer edhrecRank,
            @JsonProperty("image_url") List<String> image_urls
    ) {
        this.cardName = cardName;
        this.manaCost = manaCost;
        this.typeLine = typeLine;
        this.oracleText = oracleText;
        this.rulings = rulings;
        this.sets = sets;
        this.formatLegalities = new HashMap<>();
        formatLegalities.keySet().forEach(key ->
            this.formatLegalities.put(
                    GameFormat.valueOf(key.toUpperCase()),
                    LegalityStatus.valueOf(formatLegalities.get(key).toUpperCase())
            )
        );
        this.edhrecRank = edhrecRank;
        this.image_urls = image_urls;
    }

    public void modifyCard(String newManaCost, String newOracleText) {
        this.manaCost = newManaCost;
        this.oracleText = newOracleText;
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

    public List<CardSet> getSets() {
        return sets;
    }

    public Map<GameFormat, LegalityStatus> getFormatLegalities() {
        return formatLegalities;
    }

    public Integer getEdhrecRank() {
        return edhrecRank;
    }

    public List<String> getImage_urls() {
        return image_urls;
    }

    @Override
    public List<? extends Searchable> searchForKeywords(SearchRequest searchRequest) {
        CardSearchRequest cardSearchRequest = (CardSearchRequest)searchRequest;
        if (cardSearchRequest.getFormats() != ANY_FORMAT && !(this.formatLegalities.getOrDefault(cardSearchRequest.getFormats(), NOT_LEGAL) == LEGAL))
            return emptyList();
        return cardSearchRequest.getKeywords().stream()
                .map(String::toLowerCase)
                .allMatch(searchElement -> {
                    if (cardSearchRequest.getCardSearchRequestType() == MATCH_TITLE) {
                        return keywordMatchesTitle(searchElement);
                    } else if (cardSearchRequest.getCardSearchRequestType() == TITLE_ONLY) {
                        return keywordExistsInTitle(searchElement);
                    } else if (cardSearchRequest.getCardSearchRequestType() == INCLUDE_ORACLE) {
                        return keywordExistsInOracle(searchElement);
                    } else {
                        return keywordExistsInFullSpace(searchElement);
                    }
                })
                ? singletonList(this)
                : emptyList();
    }

    @Override
    public List<? extends Searchable> searchForKeywords(List<String> keywords) {
        return searchForKeywords(new CardSearchRequest(keywords, ANY_FORMAT, 1, INCLUDE_ORACLE));
    }

    @Override
    public List<? extends Searchable> fuzzySearchForKeywords(SearchRequest searchRequest, Integer fuzzyDistance) {
        return null; //TODO
    }

    private boolean keywordMatchesTitle(String keyword) {
        return this.cardName.equalsIgnoreCase(keyword);
    }

    private boolean keywordExistsInTitle(String keyword) {
        return this.cardName.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean keywordExistsInOracle(String keyword) {
        return keywordExistsInTitle(keyword) || this.oracleText.toLowerCase().contains(keyword.toLowerCase()) || this.typeLine.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean keywordExistsInFullSpace(String keyword) {
        return keywordExistsInOracle(keyword) || this.rulings.stream().anyMatch(ruling -> ruling.toLowerCase().contains(keyword.toLowerCase()));
    }

    @Override
    public Integer getRelevancy(List<String> keywords) {
        Integer relevancy = this.edhrecRank;
        Integer normalSets = (int)this.sets.stream().filter(set->set.getCardSetType()==NORMAL_SET).count();
        Integer foilSets = (int)this.sets.stream().filter(set->set.getCardSetType()==FOIL_ONLY_SET).count();
        relevancy -= (normalSets * 2500 + foilSets * 5000);
        if (keywords.stream().allMatch(keyword -> this.cardName.toLowerCase().contains(keyword.toLowerCase()))) {
            relevancy -= 100000;
        }
        Boolean nameStartsWithKeyword = keywords.stream()
                .anyMatch(keyword -> cardName.toLowerCase().startsWith(keyword.toLowerCase()));
        if (typeLine.contains("Legendary") && nameStartsWithKeyword) {
            relevancy -= 100000;
        }
        Boolean matchesName = keywords.stream()
                .allMatch(keyword -> cardName.toLowerCase().contains(keyword.toLowerCase()));
        if (!matchesName)
            relevancy += 10000;
        return relevancy;
    }

    @Override
    public Integer getFuzzyRelevancy(List<String> keywords, Integer fuzzyDistance) {
        return null; //TODO
    }

    @Override
    public Optional<?> findByIndex(Integer index) {
        throw new NotYetImplementedException();
    }

    @Override
    public int compareTo(Object o) {
        return cardName.compareToIgnoreCase(((Card)o).cardName);
    }
}
