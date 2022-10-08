package search;

import contract.cards.Card;
import contract.searchRequests.CardSearchRequest;
import init_utils.ManaEmojiService;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import search.contract.DiscordEmbedField;
import search.contract.DiscordReturnPayload;
import search.contract.EmbedBuilderBuilder;
import search.contract.request.DiscordCardSearchRequest;
import service.CardPriceReturnObject;
import service.CardPriceSearchService;
import service.RawCardSearchService;
import search.interaction_pagination.pagination_enum.CardDataReturnType;

import java.util.List;

import static contract.cards.GameFormat.ANY_FORMAT;
import static contract.searchRequests.CardSearchRequestType.*;
import static ingestion.card.JsonCardIngestionService.getCards;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
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
        List<Card> cards = getCards();
        cards.forEach(manaEmojiService::replaceManaSymbols);
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
                            card.getFormatLegalities().entrySet().stream()
                                    .map(entry ->
                                            new DiscordEmbedField(
                                                    prettifyEnumString(entry.getKey().toString()),
                                                    prettifyEnumString(entry.getValue().toString())
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
                                    .limit(25)
                                    .collect(toList())
                    );
        }
        return new EmbedBuilderBuilder()
                .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                .setTitle(card.getCardName() + "  " + card.getManaCost())
                .addFields(new DiscordEmbedField(card.getTypeLine(), card.getOracleText()))
                .setThumbnail(card.getImage_urls().get(0));
    }

    private String prettifyEnumString(String input) {
        return (input.substring(0, 1) + input.substring(1).toLowerCase()).replace("_", " ");
    }

    private String getFooter(DiscordCardSearchRequest searchRequest, Integer cardListSize) {
        if (searchRequest.getCardSearchRequestType() == MATCH_TITLE) {
            return "";
        }
        return join(" ", searchRequest.getKeywords()) +
                " | " + searchRequest.getCardDataReturnType().toString().toLowerCase() +
                " | Page " + (((searchRequest.getPageNumber() + cardListSize - 1) % cardListSize) + 1);
    }

    public List<SlashCommandOptionChoice> getAutocompleteSuggestions(String query) {
        if (query.length() == 0) {
            return emptyList();
        }
        CardSearchRequest searchRequest = new CardSearchRequest(
                asList(query.split(" ")),
                ANY_FORMAT,
                1,
                TITLE_ONLY
        );
        List<SlashCommandOptionChoice> rawCards = rawCardSearchService.getCardsWithOracleFallback(searchRequest).stream()
                .limit(10)
                .map(Card::getCardName)
                .map(card -> SlashCommandOptionChoice.create(card, "\"" + card + "\""))
                .collect(toList());
        rawCards.add(0, SlashCommandOptionChoice.create(query, query));
        return rawCards;
    }
}
