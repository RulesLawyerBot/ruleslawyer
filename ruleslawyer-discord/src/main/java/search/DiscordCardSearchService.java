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
import search.interaction_pagination.pagination_enum.CardPage;

import java.util.List;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static ingestion.card.JsonCardIngestionService.getCards;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static search.interaction_pagination.pagination_enum.CardPage.*;

public class DiscordCardSearchService {

    private RawCardSearchService rawCardSearchService;
    private CardPriceSearchService cardPriceSearchService;
    public static final String CARD_SEARCH_AUTHOR_TEXT = "RulesLawyer Card Search";

    public DiscordCardSearchService(ManaEmojiService manaEmojiService) {
        List<Card> cards = getCards()
                .stream()
                .map(manaEmojiService::replaceManaSymbols)
                .collect(toList());
        this.rawCardSearchService = new RawCardSearchService(cards);
        this.cardPriceSearchService = new CardPriceSearchService();
    }

    //TODO fix return formats
    public EmbedBuilder getSearchResult(String author, String query, CardPage cardPage) {
        DiscordCardSearchRequest request = new DiscordCardSearchRequest(
                asList(query.split(" ")),
                ANY_FORMAT, //TODO
                author,
                cardPage);
        return getSearchResult(request);
    }

    public EmbedBuilder getSearchResult(DiscordCardSearchRequest searchRequest) {
        List<Card> cards = rawCardSearchService.getCardsWithOracleFallback(searchRequest);
        Card card = cards.isEmpty() ? null : cards.get(0);
        return getEmbedForCard(card, searchRequest.getCardPage());
    }

    private EmbedBuilder getEmbedForCard(Card card, CardPage cardPage) {
        if (card == null) {
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setTitle("No card found")
                    .build();
        }
        if (cardPage == RULINGS) {
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setTitle(card.getCardName())
                    .addFields(
                            card.getRulings().stream()
                                    .map(ruling -> new DiscordEmbedField("Ruling", ruling))
                                    .collect(toList())
                    )
                    .setThumbnail(card.getImage_urls().get(0))
                    .setFooter(getFooter(card))
                    .build();
        }
        if (cardPage == LEGALITY) {
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
                    .setThumbnail(card.getImage_urls().get(0))
                    .setFooter(getFooter(card))
                    .build();
        }
        if (cardPage == ART) {
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setImage(card.getImage_urls().get(0))
                    .setFooter(getFooter(card))
                    .build();
        }
        if (cardPage == PRICE) {
            List<CardPriceReturnObject> prices = cardPriceSearchService.getPrices(card.getSets());
            return new EmbedBuilderBuilder()
                    .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .setTitle(card.getCardName())
                    .setFooter(getFooter(card))
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
                    )
                    .build();
        }
        return new EmbedBuilderBuilder()
                .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                .setTitle(card.getCardName() + card.getManaCost())
                .addFields(new DiscordEmbedField(card.getTypeLine(), card.getOracleText()))
                .setFooter(getFooter(card))
                .setThumbnail(card.getImage_urls().get(0))
                .build();
    }

    private String getFooter(Card card) {
        return card.getCardName();
    }
}
