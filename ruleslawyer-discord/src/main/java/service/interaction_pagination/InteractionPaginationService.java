package service.interaction_pagination;

import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import exception.NotYetImplementedException;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import search.DiscordRuleSearchService;
import search.contract.DiscordSearchRequest;
import search.contract.DiscordSearchResult;
import search.contract.builder.DiscordSearchRequestBuilder;

import java.util.List;
import java.util.NoSuchElementException;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static search.contract.builder.DiscordSearchRequestBuilder.aDiscordSearchRequest;
import static service.interaction_pagination.InteractionPaginationStatics.*;
import static service.interaction_pagination.PageDirection.*;


public class InteractionPaginationService {

    private DiscordRuleSearchService discordRuleSearchService;

    public InteractionPaginationService(DiscordRuleSearchService discordRuleSearchService) {
        this.discordRuleSearchService = discordRuleSearchService;
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
            Embed embed = message.getEmbeds().get(0);
            DiscordSearchRequest searchRequest = getSearchRequestFromEmbed(
                    embed.getTitle().get(),
                    embed.getFooter().get().getText().get()
            );
            searchRequest.getNextPage(getPaginationDirection(searchRequest, event.getMessageComponentInteraction().getCustomId()));
            DiscordSearchResult searchResult = discordRuleSearchService.getSearchResult(searchRequest);
            message.edit(searchResult.getEmbed());
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            e.printStackTrace();
        }
        event.getMessageComponentInteraction().createImmediateResponder().respond();
    }

    private DiscordSearchRequest getSearchRequestFromEmbed(String header, String footer) {
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

    private PageDirection getPaginationDirection(DiscordSearchRequest searchRequest, String commandId) {
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

    private void deleteMessage(MessageComponentCreateEvent event) {
        event.getMessageComponentInteraction().getMessage().map(Message::delete);
    }
}
