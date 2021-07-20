package search;

import contract.cards.Card;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import search.contract.DiscordEmbedField;
import search.contract.EmbedBuilderBuilder;
import search.contract.request.DiscordCardSearchRequest;
import service.RawCardSearchService;
import service.interaction_pagination.pagination_enum.CardPage;

import java.util.Optional;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static java.util.Arrays.asList;
import static service.interaction_pagination.pagination_enum.CardPage.ORACLE;

public class DiscordCardSearchService {

    private RawCardSearchService rawCardSearchService;
    public static final String CARD_SEARCH_AUTHOR_TEXT = "RulesLawyer Card Search";

    public DiscordCardSearchService() {
        this.rawCardSearchService = new RawCardSearchService();
    }

    //TODO fix retvals
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
        return getEmbedForCard(card);
    }

    private EmbedBuilder getEmbedForCard(Card card, CardPage cardPage) {
        return new EmbedBuilderBuilder().setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                .setTitle(card.getCardName() + card.getManaCost())
                .addFields(new DiscordEmbedField(card.getTypeLine(), card.getOracleText()))
                .build();
    }
}
