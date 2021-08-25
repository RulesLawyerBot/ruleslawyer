package search;

import contract.cards.Card;
import contract.cards.FormatLegality;
import init_utils.ManaEmojiService;
import search.contract.DiscordEmbedField;
import search.contract.DiscordReturnPayload;
import search.contract.EmbedBuilderBuilder;
import search.contract.request.DiscordCardSearchRequest;
import service.CardPriceReturnObject;
import service.CardPriceSearchService;
import service.RawCardSearchService;
import search.interaction_pagination.pagination_enum.CardDataReturnType;

import java.util.List;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static contract.searchRequests.CardSearchRequestType.INCLUDE_ORACLE;
import static contract.searchRequests.CardSearchRequestType.MATCH_TITLE;
import static ingestion.card.JsonCardIngestionService.getCards;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static search.interaction_pagination.InteractionPaginationStatics.*;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.*;

public class DiscordCardSearchService {

    private RawCardSearchService rawCardSearchService;
    private CardPriceSearchService cardPriceSearchService;
    public static final String CARD_SEARCH_AUTHOR_TEXT = "RulesLawyer Card Search";
    public static final String NO_CARD_FOUND_TEXT = "No card found";
    private static final EmbedBuilderBuilder NO_CARD_FOUND_EMBED = new EmbedBuilderBuilder().setAuthor(CARD_SEARCH_AUTHOR_TEXT).setTitle(NO_CARD_FOUND_TEXT);

    public DiscordCardSearchService(ManaEmojiService manaEmojiService) {
        List<Card> cards = getCards()
                .stream()
                .map(manaEmojiService::replaceManaSymbols)
                .collect(toList());
        this.rawCardSearchService = new RawCardSearchService(cards);
        this.cardPriceSearchService = new CardPriceSearchService();
    }

    public DiscordReturnPayload getSearchResult(String author, String query, CardDataReturnType cardDataReturnType) {
        DiscordCardSearchRequest request =
                (query.startsWith("\"") && query.endsWith("\"")) ?
                        new DiscordCardSearchRequest(
                                singletonList(query.substring(1, query.length()-1).toLowerCase()),
                                ANY_FORMAT, //TODO, or maybe not
                                author,
                                cardDataReturnType,
                                MATCH_TITLE,
                                1
                        ) :
                        new DiscordCardSearchRequest(
                                asList(query.split(" ")),
                                ANY_FORMAT,
                                author,
                                cardDataReturnType,
                                INCLUDE_ORACLE,
                                1
                        );
        return getSearchResult(request);
    }

    public DiscordReturnPayload getSearchResult(DiscordCardSearchRequest searchRequest) {
        List<Card> cards = rawCardSearchService.getCardsWithOracleFallback(searchRequest);
        if (cards.isEmpty()) {
            return new DiscordReturnPayload(NO_CARD_FOUND_EMBED).setComponents(DELETE_ONLY_ROW);
        }
        Card card = cards.get((searchRequest.getPageNumber()-1+cards.size()) % cards.size());
        EmbedBuilderBuilder embed = getEmbedForCard(card, searchRequest.getCardDataReturnType());
        if (searchRequest.getCardSearchRequestType() == MATCH_TITLE || cards.size() == 1) {
            return new DiscordReturnPayload(embed.setFooter(getFooter(searchRequest, cards.size()))).setComponents(CARD_ROW, DELETE_ONLY_ROW);
        }
        return new DiscordReturnPayload(embed.setFooter(getFooter(searchRequest, cards.size()))).setComponents(CARD_ROW, CARD_PAGINATION_ROW);
    }

    private EmbedBuilderBuilder getEmbedForCard(Card card, CardDataReturnType cardDataReturnType) {
        if (cardDataReturnType == RULINGS) {
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setTitle(card.getCardName())
                    .addFields(
                            card.getRulings().size() > 0 ?
                            card.getRulings().stream()
                                    .map(ruling -> new DiscordEmbedField("Ruling", ruling))
                                    .collect(toList()) :
                            singletonList(new DiscordEmbedField(card.getCardName(), "No rulings for this card"))
                    )
                    .setThumbnail(card.getImage_urls().get(0));
        }
        if (cardDataReturnType == LEGALITY) {
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setTitle(card.getCardName())
                    .addFields(
                            stream(FormatLegality.values())
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
        if (searchRequest.getCardSearchRequestType() == MATCH_TITLE) {
            return searchRequest.getRequester() +
                    " | " + "\"" + searchRequest.getKeywords().get(0) + "\"" +
                    " | " + searchRequest.getCardDataReturnType().toString().toLowerCase() +
                    " | " + "exact match query";
        }
        return searchRequest.getRequester() +
                " | " + join(" ", searchRequest.getKeywords()) +
                " | " + searchRequest.getCardDataReturnType().toString().toLowerCase() +
                " | Page " + (((searchRequest.getPageNumber() + cardListSize - 1) % cardListSize) + 1);
    }
}
