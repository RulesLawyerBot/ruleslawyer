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
import static java.util.Optional.empty;
import static search.DiscordCardSearchService.CARD_SEARCH_AUTHOR_TEXT;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.PRICE;
import static search.interaction_pagination.pagination_enum.CardPageDirection.NEXT_CARD;

public class CardPaginationService {

    private DiscordCardSearchService discordCardSearchService;

    public CardPaginationService(DiscordCardSearchService discordCardSearchService) {
        this.discordCardSearchService = discordCardSearchService;
    }

    protected Optional<DiscordReturnPayload> paginateCard(MessageComponentCreateEvent event) {
        if (!hasFooter(event)) {
            event.getMessageComponentInteraction().createImmediateResponder().respond();
            return empty();
        }
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
            DiscordCardSearchRequest searchRequest = getCardSearchRequestFromFooter(event.getMessageComponentInteraction().getMessage().getEmbeds().get(0));
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
            DiscordCardSearchRequest searchRequest = getCardSearchRequestFromFooter(event.getMessageComponentInteraction().getMessage().getEmbeds().get(0));
            searchRequest.setCardDataReturnType(cardDataReturnType);
            return Optional.of(discordCardSearchService.getSearchResult(searchRequest));
        }
    }

    private Optional<DiscordReturnPayload> paginateCardNumber(MessageComponentCreateEvent event, CardPageDirection cardPageDirection) {
        DiscordCardSearchRequest searchRequest = getCardSearchRequestFromFooter(event.getMessageComponentInteraction().getMessage().getEmbeds().get(0));
        searchRequest.paginateSearchRequest(cardPageDirection);
        event.getMessageComponentInteraction().createImmediateResponder().respond();
        return Optional.of(discordCardSearchService.getSearchResult(searchRequest));
    }

    private boolean hasFooter(MessageComponentCreateEvent event) {
        Message message = event.getMessageComponentInteraction().getMessage();
        List<Embed> embeds = message.getEmbeds();
        if (embeds.size() < 1) {
            return false;
        }
        Optional<EmbedFooter> embedFooterOptional = embeds.get(0).getFooter();
        if (!embedFooterOptional.isPresent()) {
            return false;
        }
        return embedFooterOptional.map(footer -> footer.getText().isPresent()).orElse(false);
    }

    private DiscordCardSearchRequest getCardSearchRequestFromFooter(Embed embed) {
        List<String> footerParts = asList(embed.getFooter().get().getText().get().split(" \\| "));
        if (footerParts.get(0).startsWith("\"")) {
            return new DiscordCardSearchRequest(
                    singletonList(footerParts.get(0).substring(1, footerParts.get(0).length()-1).toLowerCase()),
                    ANY_FORMAT,
                    null,
                    CardDataReturnType.valueOf(footerParts.get(1).toUpperCase()),
                    MATCH_TITLE,
                    1
            );
        }
        return new DiscordCardSearchRequest(
                asList(footerParts.get(0).split(" ")),
                ANY_FORMAT,
                null,
                CardDataReturnType.valueOf(footerParts.get(1).toUpperCase()),
                INCLUDE_ORACLE,
                parseInt(footerParts.get(2).substring(5))
        );
    }
}
