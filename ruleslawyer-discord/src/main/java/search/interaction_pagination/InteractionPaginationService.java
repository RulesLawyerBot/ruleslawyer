package search.interaction_pagination;

import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import exception.NotYetImplementedException;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import search.DiscordCardSearchService;
import search.DiscordRuleSearchService;
import search.contract.DiscordReturnPayload;
import search.contract.EmbedBuilderBuilder;
import search.contract.request.DiscordCardSearchRequest;
import search.contract.request.DiscordRuleSearchRequest;
import search.contract.request.builder.DiscordSearchRequestBuilder;
import search.interaction_pagination.pagination_enum.CardDataReturnType;
import search.interaction_pagination.pagination_enum.CardPageDirection;
import search.interaction_pagination.pagination_enum.PageDirection;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static search.DiscordCardSearchService.CARD_SEARCH_AUTHOR_TEXT;
import static search.DiscordRuleSearchService.RULE_SEARCH_AUTHOR_TEXT;
import static search.contract.request.builder.DiscordSearchRequestBuilder.aDiscordSearchRequest;
import static search.interaction_pagination.InteractionPaginationStatics.*;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.PRICE;
import static search.interaction_pagination.pagination_enum.CardPageDirection.NEXT_CARD;
import static search.interaction_pagination.pagination_enum.PageDirection.*;

public class InteractionPaginationService {

    private DiscordRuleSearchService discordRuleSearchService;
    private DiscordCardSearchService discordCardSearchService;

    public InteractionPaginationService(DiscordRuleSearchService discordRuleSearchService, DiscordCardSearchService discordCardSearchService) {
        this.discordRuleSearchService = discordRuleSearchService;
        this.discordCardSearchService = discordCardSearchService;
    }

    public void respondToInteractionCommand(MessageComponentCreateEvent event) {
        String id = event.getMessageComponentInteraction().getCustomId();

        switch (id) {
            case DELETE_STRING:
                deleteMessage(event);
                break;
            case WEBSITE_LINK_STRING:
                throw new NotYetImplementedException();
            default:
                paginate(event);
                break;
        }
    }

    private void paginate(MessageComponentCreateEvent event) {
        try {
            Message message = event.getMessageComponentInteraction().getMessage().get();
            String authorText = message.getEmbeds().get(0).getAuthor().get().getName();

            Optional<DiscordReturnPayload> output = empty();
            if (authorText.equals(RULE_SEARCH_AUTHOR_TEXT)) {
                output = paginateRules(event);
                event.getMessageComponentInteraction().createImmediateResponder().respond();
            }
            if (authorText.equals(CARD_SEARCH_AUTHOR_TEXT)) {
                output = paginateCard(event);
            }
            output.map(i -> message.edit(i.getEmbed()));
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            e.printStackTrace();
        };
    }

    private Optional<DiscordReturnPayload> paginateRules(MessageComponentCreateEvent event) {
        Message message = event.getMessageComponentInteraction().getMessage().get();
        Embed embed = message.getEmbeds().get(0);
        DiscordRuleSearchRequest searchRequest = getRuleSearchRequestFromEmbed(
                embed.getTitle().get(),
                embed.getFooter().get().getText().get()
        );
        searchRequest.getNextPage(getRulePaginationDirection(searchRequest, event.getMessageComponentInteraction().getCustomId()));
        return Optional.of(discordRuleSearchService.getSearchResult(searchRequest));
    }

    private DiscordRuleSearchRequest getRuleSearchRequestFromEmbed(String header, String footer) {
        DiscordSearchRequestBuilder discordSearchRequest = aDiscordSearchRequest();
        List<String> headerParts = asList(header.split(" \\| "));
        headerParts.subList(0, headerParts.size()-1).forEach(
                headerPart -> addHeaderPartsToRequest(discordSearchRequest, headerPart)
        );
        discordSearchRequest.appendKeywords(
                asList(headerParts.get(headerParts.size()-1).split("/"))
        );

        List<String> footerParts = asList(footer.split(" \\| "));
        discordSearchRequest.setRequester(footerParts.get(0).substring("Requested by: ".length()));
        discordSearchRequest.setPageNumber(parseInt(asList(footerParts.get(1).split(" ")).get(1))-1);
        return discordSearchRequest
                .build();
    }

    private void addHeaderPartsToRequest(DiscordSearchRequestBuilder discordSearchRequest, String headerPart) {
        try {
            discordSearchRequest.setRuleSource(RuleSource.valueOf(headerPart));
        } catch (IllegalArgumentException ignored) {
            discordSearchRequest.setRuleRequestCategory(RuleRequestCategory.valueOf(headerPart.toUpperCase()));
        }
    }

    private PageDirection getRulePaginationDirection(DiscordRuleSearchRequest searchRequest, String commandId) {
        if (commandId.equals(LEFT_PAGINATION_STRING)) {
            return PREVIOUS_PAGE;
        } else if (commandId.equals(RIGHT_PAGINATION_STRING)) {
            return NEXT_PAGE;
        } else if (searchRequest.getRuleRequestCategory() == DIGITAL) {
            return TO_PAPER;
        } else {
            return TO_DIGITAL;
        }
    }

    private Optional<DiscordReturnPayload> paginateCard(MessageComponentCreateEvent event) {
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
            event.getMessageComponentInteraction().createImmediateResponder().respond(); //TODO remove this in a future javacord version
            DiscordCardSearchRequest searchRequest = getSearchRequestFromFooter(event.getMessageComponentInteraction().getMessage().get().getEmbeds().get(0));
            searchRequest.setCardDataReturnType(cardDataReturnType);
            event.getMessageComponentInteraction().getMessage().get().edit(
                    new EmbedBuilderBuilder()
                            .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                            .setTitle("Thinking...")
                            .build()
            );
            //event.getMessageComponentInteraction().respondLater(); this is bugged
            EmbedBuilder embed = discordCardSearchService.getSearchResult(searchRequest);
            //event.getMessageComponentInteraction().createFollowupMessageBuilder().send(); this is bugged
            return Optional.of(new DiscordReturnPayload(embed));
        } else {
            event.getMessageComponentInteraction().createImmediateResponder().respond();
            DiscordCardSearchRequest searchRequest = getSearchRequestFromFooter(event.getMessageComponentInteraction().getMessage().get().getEmbeds().get(0));
            searchRequest.setCardDataReturnType(cardDataReturnType);
            EmbedBuilder embed = discordCardSearchService.getSearchResult(searchRequest);
            return Optional.of(new DiscordReturnPayload(embed));
        }
    }

    private Optional<DiscordReturnPayload> paginateCardNumber(MessageComponentCreateEvent event, CardPageDirection cardPageDirection) {
        DiscordCardSearchRequest searchRequest = getSearchRequestFromFooter(event.getMessageComponentInteraction().getMessage().get().getEmbeds().get(0));
        searchRequest.paginateSearchRequest(cardPageDirection);
        EmbedBuilder embed = discordCardSearchService.getSearchResult(searchRequest);
        event.getMessageComponentInteraction().createImmediateResponder().respond();
        return Optional.of(new DiscordReturnPayload(embed));
    }

    private boolean hasFooter(MessageComponentCreateEvent event) {
        Optional<Message> messageOptional = event.getMessageComponentInteraction().getMessage();
        if (!messageOptional.isPresent()) {
            return false;
        }
        List<Embed> embeds = messageOptional.get().getEmbeds();
        if (embeds.size() < 1) {
            return false;
        }
        Optional<EmbedFooter> embedFooterOptional = embeds.get(0).getFooter();
        if (!embedFooterOptional.isPresent()) {
            return false;
        }
        return embedFooterOptional.map(footer -> footer.getText().isPresent()).orElse(false);
    }

    private DiscordCardSearchRequest getSearchRequestFromFooter(Embed embed) {
        List<String> footerParts = asList(embed.getFooter().get().getText().get().split(" \\| "));
        return new DiscordCardSearchRequest(
                asList(footerParts.get(1).split(" ")),
                ANY_FORMAT,
                footerParts.get(0),
                CardDataReturnType .valueOf(footerParts.get(2).toUpperCase()),
                parseInt(footerParts.get(3).substring(5))
        );
    }

    private void deleteMessage(MessageComponentCreateEvent event) {
        event.getMessageComponentInteraction().getMessage().map(Message::delete);
    }
}
