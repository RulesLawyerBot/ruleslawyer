package search.interaction_pagination;

import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import exception.NotYetImplementedException;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import search.DiscordCardSearchService;
import search.DiscordRuleSearchService;
import search.contract.DiscordReturnPayload;
import search.contract.EmbedBuilderBuilder;
import search.contract.request.DiscordRuleSearchRequest;
import search.contract.request.builder.DiscordSearchRequestBuilder;
import search.interaction_pagination.pagination_enum.CardPage;
import search.interaction_pagination.pagination_enum.PageDirection;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static search.DiscordCardSearchService.CARD_SEARCH_AUTHOR_TEXT;
import static search.DiscordRuleSearchService.RULE_SEARCH_AUTHOR_TEXT;
import static search.contract.request.builder.DiscordSearchRequestBuilder.aDiscordSearchRequest;
import static search.interaction_pagination.InteractionPaginationStatics.*;
import static search.interaction_pagination.pagination_enum.CardPage.ORACLE;
import static search.interaction_pagination.pagination_enum.CardPage.PRICE;
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
                output = Optional.of(paginateRules(event));
                event.getMessageComponentInteraction().createImmediateResponder().respond();
            }
            if (authorText.equals(CARD_SEARCH_AUTHOR_TEXT)) {
                output = Optional.of(paginateCard(event));
            }
            output.map(i -> message.edit(i.getEmbed()));
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            e.printStackTrace();
        };
    }

    private DiscordReturnPayload paginateRules(MessageComponentCreateEvent event) {
        Message message = event.getMessageComponentInteraction().getMessage().get();
        Embed embed = message.getEmbeds().get(0);
        DiscordRuleSearchRequest searchRequest = getRuleSearchRequestFromEmbed(
                embed.getTitle().get(),
                embed.getFooter().get().getText().get()
        );
        searchRequest.getNextPage(getRulePaginationDirection(searchRequest, event.getMessageComponentInteraction().getCustomId()));
        return discordRuleSearchService.getSearchResult(searchRequest);
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

    private DiscordReturnPayload paginateCard(MessageComponentCreateEvent event) {
        CardPage cardPage = Optional.of(event.getMessageComponentInteraction().getCustomId()).map(CardPage::valueOf).orElse(ORACLE);
        if (cardPage == PRICE) {
            event.getMessageComponentInteraction().createImmediateResponder().respond(); //TODO remove this in a future javacord version
            String cardName = getCardNameFromFooter(event.getMessageComponentInteraction().getMessage().get().getEmbeds().get(0));
            event.getMessageComponentInteraction().getMessage().get().edit(
                    new EmbedBuilderBuilder()
                            .setAuthor(CARD_SEARCH_AUTHOR_TEXT)
                            .setTitle("Thinking...")
                            .setFooter(cardName)
                            .build()
            );
            //event.getMessageComponentInteraction().respondLater(); this is bugged
            EmbedBuilder embed = discordCardSearchService.getSearchResult(
                    "",
                    cardName,
                    cardPage
            );
            //event.getMessageComponentInteraction().createFollowupMessageBuilder().send(); this is bugged
            return new DiscordReturnPayload(embed);
        } else {
            event.getMessageComponentInteraction().createImmediateResponder().respond();
            EmbedBuilder embed = discordCardSearchService.getSearchResult(
                    "",
                    getCardNameFromFooter(event.getMessageComponentInteraction().getMessage().get().getEmbeds().get(0)),
                    cardPage
            );
            return new DiscordReturnPayload(embed);
        }
    }

    private String getCardNameFromFooter(Embed embed) {
        if (!embed.getFooter().isPresent()) {
            return "";
        }
        return embed.getFooter().get().getText().get();
    }

    private void deleteMessage(MessageComponentCreateEvent event) {
        event.getMessageComponentInteraction().getMessage().map(Message::delete);
    }
}
