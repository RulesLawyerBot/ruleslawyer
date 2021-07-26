package contract.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import contract.Searchable;
import contract.searchRequests.CardSearchRequest;
import contract.searchRequests.SearchRequest;
import exception.NotYetImplementedException;

import java.util.List;
import java.util.Optional;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static contract.searchRequests.CardSearchRequestType.INCLUDE_ORACLE;
import static contract.searchRequests.CardSearchRequestType.TITLE_ONLY;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class Card implements Searchable {

    private String cardName;
    private String manaCost;
    private String typeLine;
    private String oracleText;
    private List<String> rulings;
    private List<String> sets;
    private List<FormatLegality> formatLegalities;
    private Integer edhrecRank;
    private String scryfallUri;
    private List<String> image_urls;

    public Card(
            @JsonProperty("cardName") String cardName,
            @JsonProperty("manaCost") String manaCost,
            @JsonProperty("typeLine") String typeLine,
            @JsonProperty("oracleText") String oracleText,
            @JsonProperty("rulings") List<String> rulings,
            @JsonProperty("sets") List<String> sets,
            @JsonProperty("legalities") List<String> formatLegalities,
            @JsonProperty("edhrec_rank") Integer edhrecRank,
            @JsonProperty("scryfall_uri") String scryfallUri,
            @JsonProperty("image_url") List<String> image_urls
    ) {
        this.cardName = cardName;
        this.manaCost = manaCost;
        this.typeLine = typeLine;
        this.oracleText = oracleText;
        this.rulings = rulings;
        this.sets = sets;
        this.formatLegalities = formatLegalities.stream()
                .map(str -> FormatLegality.valueOf(str.toUpperCase()))
                .collect(toList());
        this.edhrecRank = edhrecRank;
        this.scryfallUri = scryfallUri;
        this.image_urls = image_urls;
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

    public List<String> getSets() {
        return sets;
    }

    public List<FormatLegality> getFormatLegalities() {
        return formatLegalities;
    }

    public Integer getEdhrecRank() {
        return edhrecRank;
    }

    public String getScryfallUri() {
        return scryfallUri;
    }

    public List<String> getImage_urls() {
        return image_urls;
    }

    @Override
    public List<? extends Searchable> searchForKeywords(SearchRequest searchRequest) {
        CardSearchRequest cardSearchRequest = (CardSearchRequest)searchRequest;
        if (cardSearchRequest.getFormatLegality() != ANY_FORMAT && !this.formatLegalities.contains(cardSearchRequest.getFormatLegality()))
            return emptyList();
        return cardSearchRequest.getKeywords().stream()
                .map(String::toLowerCase)
                .allMatch(searchElement -> {
                    if (cardSearchRequest.getCardSearchRequestType() == TITLE_ONLY) {
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
        return searchForKeywords(new CardSearchRequest(keywords, ANY_FORMAT));
    }

    @Override
    public List<? extends Searchable> fuzzySearchForKeywords(SearchRequest searchRequest, Integer fuzzyDistance) {
        return null; //TODO
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
        Boolean nameStartsWithKeyword = keywords.stream()
                .anyMatch(keyword -> cardName.toLowerCase().startsWith(keyword.toLowerCase()));
        if (typeLine.contains("Legendary") && nameStartsWithKeyword) {
            relevancy-=10000;
        }
        Boolean matchesName = keywords.stream()
                .allMatch(keyword -> cardName.toLowerCase().contains(keyword.toLowerCase()));
        if (!matchesName)
            relevancy += 10000;
        relevancy -= (this.sets.size()*1000);
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
