package search;

import contract.cards.Card;
import contract.cards.FormatLegality;
import init_utils.ManaEmojiService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import search.contract.DiscordEmbedField;
import search.contract.EmbedBuilderBuilder;
import search.contract.request.DiscordCardSearchRequest;
import service.CardPriceReturnObject;
import service.CardPriceSearchService;
import service.RawCardSearchService;
import search.interaction_pagination.pagination_enum.CardDataReturnType;

import java.util.List;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static ingestion.card.JsonCardIngestionService.getCards;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.*;

public class DiscordCardSearchService {

    private RawCardSearchService rawCardSearchService;
    private CardPriceSearchService cardPriceSearchService;
    public static final String CARD_SEARCH_AUTHOR_TEXT = "RulesLawyer Card Search";
    private static final String NO_CARD_FOUND_TEXT = "No card found";

    public DiscordCardSearchService(ManaEmojiService manaEmojiService) {
        List<Card> cards = getCards()
                .stream()
                .map(manaEmojiService::replaceManaSymbols)
                .collect(toList());
        this.rawCardSearchService = new RawCardSearchService(cards);
        this.cardPriceSearchService = new CardPriceSearchService();
    }

    public EmbedBuilder getSearchResult(String author, String query, CardDataReturnType cardDataReturnType) {
        DiscordCardSearchRequest request = new DiscordCardSearchRequest(
                asList(query.split("\\P{Alpha}+")),
                ANY_FORMAT, //TODO, or maybe not
                author,
                cardDataReturnType,
                1
        );
        return getSearchResult(request);
    }

    public EmbedBuilder getSearchResult(DiscordCardSearchRequest searchRequest) {
        List<Card> cards = rawCardSearchService.getCardsWithOracleFallback(searchRequest);
        Card card = cards.isEmpty() ?
                null :
                cards.get(
                        (searchRequest.getPageNumber()-1+cards.size()) % cards.size()
                );
        EmbedBuilderBuilder embedBuilder = getEmbedForCard(card, searchRequest.getCardDataReturnType());
        return cards.isEmpty() ? embedBuilder.build() : embedBuilder.setFooter(getFooter(searchRequest, cards.size())).build();
    }

    private EmbedBuilderBuilder getEmbedForCard(Card card, CardDataReturnType cardDataReturnType) {
        if (card == null) {
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setTitle(NO_CARD_FOUND_TEXT);
        }
        if (cardDataReturnType == RULINGS) {
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setTitle(card.getCardName())
                    .addFields(
                            card.getRulings().stream()
                                    .map(ruling -> new DiscordEmbedField("Ruling", ruling))
                                    .collect(toList())
                    )
                    .setThumbnail(card.getImage_urls().get(0));
        }
        if (cardDataReturnType == LEGALITY) {
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setTitle(card.getCardName())
                    .addFields(
                            asList(FormatLegality.values()).stream()
                                    .filter(format -> format != ANY_FORMAT)
                                    .map(
                                            format ->
                                                    new DiscordEmbedField(
                                                            format.name().substring(0, 1) + format.name().substring(1).toLowerCase(),
                                                            card.getFormatLegalities().contains(format) ?
                                                                    "Legal" : "Not legal"
                                                    )
                                    )
                                    .collect(toList())
                    )
                    .setHasInlineFields(true)
                    .setThumbnail(card.getImage_urls().get(0));
        }
        if (cardDataReturnType == ART) {
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setImage(card.getImage_urls().get(0));
        }
        if (cardDataReturnType == PRICE) {
            List<CardPriceReturnObject> prices = cardPriceSearchService.getPrices(card.getSets());
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setTitle(card.getCardName())
                    .setHasInlineFields(true)
                    .setThumbnail(card.getImage_urls().get(0))
                    .addFields(
                            prices.stream()
                                    .filter(price -> price.getCardPrices().length() > 1)
                                    .map(price ->
                                            new DiscordEmbedField(
                                                    price.getCardSetName(),
                                                    price.getCardPrices()
                                            )
                                    )
                                    .collect(toList())
                    );
        }
        return new EmbedBuilderBuilder()
                .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                .setTitle(card.getCardName() + card.getManaCost())
                .addFields(new DiscordEmbedField(card.getTypeLine(), card.getOracleText()))
                .setThumbnail(card.getImage_urls().get(0));
    }

    private String getFooter(DiscordCardSearchRequest searchRequest, Integer cardListSize) {
        return searchRequest.getRequester() +
                " | " + join(" ", searchRequest.getKeywords()) +
                " | " + searchRequest.getCardDataReturnType().toString().toLowerCase() +
                " | Page " + (((searchRequest.getPageNumber() + cardListSize - 1) % cardListSize) + 1);
    }
}
