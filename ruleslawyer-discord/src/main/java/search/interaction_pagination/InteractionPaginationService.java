package search.interaction_pagination;

import exception.NotYetImplementedException;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import search.DiscordCardSearchService;
import search.DiscordRuleSearchService;
import search.contract.DiscordReturnPayload;

import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Optional.empty;
import static search.DiscordCardSearchService.CARD_SEARCH_AUTHOR_TEXT;
import static search.DiscordRuleSearchService.RULE_SEARCH_AUTHOR_TEXT;
import static search.interaction_pagination.InteractionPaginationStatics.*;

public class InteractionPaginationService {

    private RulePaginationService rulePaginationService;
    private CardPaginationService cardPaginationService;

    public InteractionPaginationService(DiscordRuleSearchService discordRuleSearchService, DiscordCardSearchService discordCardSearchService) {
        this.rulePaginationService = new RulePaginationService(discordRuleSearchService);
        this.cardPaginationService = new CardPaginationService(discordCardSearchService);
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
            Message message = event.getMessageComponentInteraction().getMessage();
            String authorText = message.getEmbeds().get(0).getAuthor().get().getName();

            Optional<DiscordReturnPayload> output = empty();
            if (authorText.equals(RULE_SEARCH_AUTHOR_TEXT)) {
                output = rulePaginationService.paginateRules(event);
                event.getMessageComponentInteraction().createImmediateResponder().respond();
            }
            if (authorText.equals(CARD_SEARCH_AUTHOR_TEXT)) {
                output = cardPaginationService.paginateCard(event);
            }
            output.map(i -> message.edit(i.getEmbed().build()));
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            e.printStackTrace();
        };
    }

    private void deleteMessage(MessageComponentCreateEvent event) {
        event.getMessageComponentInteraction().getMessage().delete();
    }
}
