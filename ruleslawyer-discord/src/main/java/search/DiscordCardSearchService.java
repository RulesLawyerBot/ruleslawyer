package search;

import contract.cards.Card;
import contract.cards.FormatLegality;
import exception.NotYetImplementedException;
import init_utils.ManaEmojiService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import search.contract.DiscordEmbedField;
import search.contract.EmbedBuilderBuilder;
import search.contract.request.DiscordCardSearchRequest;
import service.RawCardSearchService;
import service.interaction_pagination.pagination_enum.CardPage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static ingestion.card.JsonCardIngestionService.getCards;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static service.interaction_pagination.pagination_enum.CardPage.*;

public class DiscordCardSearchService {

    private RawCardSearchService rawCardSearchService;
    public static final String CARD_SEARCH_AUTHOR_TEXT = "RulesLawyer Card Search";

    public DiscordCardSearchService(ManaEmojiService manaEmojiService) {
        List<Card> cards = getCards()
                .stream()
                .map(manaEmojiService::replaceManaSymbols)
                .collect(toList());
        this.rawCardSearchService = new RawCardSearchService(cards);
    }

    //TODO fix return formats
    public EmbedBuilder getSearchResult(String author, String query, Optional<String> cardPage) {
        DiscordCardSearchRequest request = new DiscordCardSearchRequest(
                asList(query.split(" ")),
                ANY_FORMAT, //TODO
                author,
                "", //TODO
                cardPage.map(CardPage::valueOf).orElse(ORACLE));
        return getSearchResult(request);
    }

    public EmbedBuilder getSearchResult(DiscordCardSearchRequest searchRequest) {
        Card card = rawCardSearchService.getCardsWithOracleFallback(searchRequest).get(0);
        return getEmbedForCard(card, searchRequest.getCardPage());
    }

    private EmbedBuilder getEmbedForCard(Card card, CardPage cardPage) {
        if (cardPage == RULINGS) {
            return new EmbedBuilderBuilder().setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .addFields(
                            card.getRulings().stream()
                                    .map(ruling -> new DiscordEmbedField("Ruling", ruling))
                                    .collect(toList())
                    )
                    .setFooter(getFooter(card))
                    .build();
        }
        if (cardPage == LEGALITY) {
            return new EmbedBuilderBuilder().setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                    .addFields(
                            asList(FormatLegality.values()).stream()
                                    .filter(format -> format != ANY_FORMAT)
                                    .map(
                                            format ->
                                                    new DiscordEmbedField(
                                                            format.name(),
                                                            card.getFormatLegalities().contains(format) ?
                                                                    "Legal" : "Not legal"
                                                    )
                                    )
                                    .collect(toList())
                    )
                    .setHasInlineFields(true)
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
            try {
                URL url = new URL(card.getScryfallUri());
            } catch (MalformedURLException e) {
                return new EmbedBuilderBuilder()
                        .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                        .addFields(new DiscordEmbedField("Error", getStackTrace(e)))
                        .setFooter(getFooter(card))
                        .build();
            }
            throw new NotYetImplementedException();
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
