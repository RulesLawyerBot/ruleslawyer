package search.interaction_pagination;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import search.DiscordCardSearchService;
import search.contract.DiscordReturnPayload;
import search.contract.EmbedBuilderBuilder;
import search.contract.request.DiscordCardSearchRequest;
import search.interaction_pagination.pagination_enum.CardDataReturnType;
import search.interaction_pagination.pagination_enum.CardPageDirection;

import java.util.List;
import java.util.Optional;

import static contract.cards.GameFormat.ANY_FORMAT;
import static contract.searchRequests.CardSearchRequestType.INCLUDE_ORACLE;
import static contract.searchRequests.CardSearchRequestType.MATCH_TITLE;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static search.DiscordCardSearchService.CARD_SEARCH_AUTHOR_TEXT;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.ORACLE;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.PRICE;
import static search.interaction_pagination.pagination_enum.CardPageDirection.NEXT_CARD;

public class CardPaginationService {

    private DiscordCardSearchService discordCardSearchService;

    public CardPaginationService(DiscordCardSearchService discordCardSearchService) {
        this.discordCardSearchService = discordCardSearchService;
    }

    protected Optional<DiscordReturnPayload> paginateCard(MessageComponentCreateEvent event) {
        try {
            CardDataReturnType cardDataReturnType = Optional.of(event.getMessageComponentInteraction().getCustomId()).map(CardDataReturnType::valueOf).orElse(null);
            return paginateCardDataReturnType(event, cardDataReturnType);
        } catch (IllegalArgumentException ignored) {
            CardPageDirection cardPageDirection = Optional.of(event.getMessageComponentInteraction().getCustomId()).map(CardPageDirection::valueOf).orElse(NEXT_CARD);
            return paginateCardNumber(event, cardPageDirection);
        }
    }

    private Optional<DiscordReturnPayload> paginateCardDataReturnType(MessageComponentCreateEvent event, CardDataReturnType cardDataReturnType) {
        if (cardDataReturnType == PRICE) {
            event.getMessageComponentInteraction().createImmediateResponder().respond();
            DiscordCardSearchRequest searchRequest = getCardSearchRequestFromEmbed(event.getMessageComponentInteraction().getMessage().getEmbeds().get(0));
            searchRequest.setCardDataReturnType(cardDataReturnType);
            event.getMessageComponentInteraction().getMessage().edit(
                    new EmbedBuilderBuilder()
                            .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                            .setTitle("Thinking...")
                            .build()
            );
            return Optional.of(discordCardSearchService.getSearchResult(searchRequest));
        } else {
            event.getMessageComponentInteraction().createImmediateResponder().respond();
            DiscordCardSearchRequest searchRequest = getCardSearchRequestFromEmbed(event.getMessageComponentInteraction().getMessage().getEmbeds().get(0));
            searchRequest.setCardDataReturnType(cardDataReturnType);
            return Optional.of(discordCardSearchService.getSearchResult(searchRequest));
        }
    }

    private Optional<DiscordReturnPayload> paginateCardNumber(MessageComponentCreateEvent event, CardPageDirection cardPageDirection) {
        DiscordCardSearchRequest searchRequest = getCardSearchRequestFromEmbed(event.getMessageComponentInteraction().getMessage().getEmbeds().get(0));
        searchRequest.paginateSearchRequest(cardPageDirection);
        event.getMessageComponentInteraction().createImmediateResponder().respond();
        return Optional.of(discordCardSearchService.getSearchResult(searchRequest));
    }

    private DiscordCardSearchRequest getCardSearchRequestFromEmbed(Embed embed) {
        if (embed.getFooter().isPresent()) {
            List<String> footerParts = asList(embed.getFooter().get().getText().get().split(" \\| "));
            return new DiscordCardSearchRequest(
                    asList(footerParts.get(0).split(" ")),
                    ANY_FORMAT,
                    null,
                    CardDataReturnType.valueOf(footerParts.get(1).toUpperCase()),
                    INCLUDE_ORACLE,
                    parseInt(footerParts.get(2).substring(5))
            );
        }
        String cardName = embed.getTitle().get();
        if (cardName.contains("  ")) {
            cardName = cardName.substring(0, cardName.indexOf("  "));
        }
        return new DiscordCardSearchRequest(
                singletonList(cardName),
                ANY_FORMAT,
                null,
                ORACLE,
                MATCH_TITLE,
                1
        );
    }
}
