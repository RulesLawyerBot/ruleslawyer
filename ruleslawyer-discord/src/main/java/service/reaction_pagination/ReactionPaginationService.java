package service.reaction_pagination;

import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;
import search.DiscordRuleSearchService;
import search.contract.request.DiscordRuleSearchRequest;
import search.contract.DiscordReturnPayload;
import search.contract.request.builder.DiscordSearchRequestBuilder;
import service.MessageDeletionService;
import service.MessageLoggingService;
import service.interaction_pagination.pagination_enum.PageDirection;

import java.util.List;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static service.interaction_pagination.pagination_enum.PageDirection.*;
import static search.contract.request.builder.DiscordSearchRequestBuilder.aDiscordSearchRequest;
import static utils.DiscordUtils.*;
import static utils.StaticEmojis.*;

@Deprecated
public class ReactionPaginationService {

    private DiscordRuleSearchService searchService;
    private MessageLoggingService messageLoggingService;

    public ReactionPaginationService(DiscordRuleSearchService searchService, MessageLoggingService messageLoggingService) {
        this.searchService = searchService;
        this.messageLoggingService = messageLoggingService;
    }

    private Optional<PageDirection> getPaginationDirection(SingleReactionEvent event) {
        if (isOwnReaction(event) || !isOwnMessage(event)) {
            return empty();
        }
        // I promise this is cleaner than the alternative
        if (event.getEmoji().equalsEmoji(LEFT_EMOJI))
            return Optional.of(PREVIOUS_PAGE);
        if (event.getEmoji().equalsEmoji(RIGHT_EMOJI))
            return Optional.of(NEXT_PAGE);
        if (event.getEmoji().equalsEmoji(PAPER_EMOJI)) {
            return Optional.of(TO_PAPER);
        }
        if (event.getEmoji().equalsEmoji(DIGITAL_EMOJI)) {
            return Optional.of(TO_DIGITAL);
        }
        return empty();
    }

    public DiscordRuleSearchRequest getSearchRequestFromEmbed(String header, String footer) {
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

    private boolean hasPaginationPermissions(DiscordRuleSearchRequest searchRequest, SingleReactionEvent event) {
        return event.getUser().map(
                    user -> (event.getServer().isPresent()
                            && event.getServer().get().canBanUsers(user)) ||
                            user.isBotOwner()
                ).orElse(false) ||
                getUsernameForReactionAddEvent(event).map(
                        name -> name.equals(searchRequest.getRequester())
                ).orElse(false);
    }

    public void placePaginationReactions(MessageCreateEvent event) {
        event.getMessage().addReaction("javacord:" + MessageDeletionService.DELETE_EMOTE_ID);
        if (shouldPlacePaginationReactions(event)) {
            event.getMessage().addReaction(LEFT_EMOJI);
            event.getMessage().addReaction(RIGHT_EMOJI);
        }
        if (getSourceChangeReaction(event).isPresent()) {
            event.getMessage().addReaction(getSourceChangeReaction(event).get());
        }
    }

    private boolean shouldPlacePaginationReactions(MessageCreateEvent event) {
        return isOwnMessage(event) && event.getMessage().getEmbeds().size() == 1
                && event.getMessage().getEmbeds().get(0).getTitle().isPresent();
    }

    public void replaceSourceChangeReactions(MessageEditEvent event) {
        Optional<String> reaction = getSourceChangeReaction(event);
        if (!reaction.isPresent()) {
            return;
        }
        if (reaction.get().equals(PAPER_EMOJI)) {
            event.getMessage().get().removeReactionByEmoji(DIGITAL_EMOJI);
            event.getMessage().get().addReaction(PAPER_EMOJI);
        }
        if (reaction.get().equals(DIGITAL_EMOJI)) {
            event.getMessage().get().removeReactionByEmoji(PAPER_EMOJI);
            event.getMessage().get().addReaction(DIGITAL_EMOJI);
        }
    }

    private Optional<String> getSourceChangeReaction(MessageCreateEvent event) {
        return messageHasFooter(event.getMessage()) ?
                getSourceChangeReaction(event.getMessage().getEmbeds().get(0)) :
                empty();
    }

    private Optional<String> getSourceChangeReaction(MessageEditEvent event) {
        return event.getMessage().map(this::messageHasFooter).orElse(false) ?
                getSourceChangeReaction(event.getMessage().get().getEmbeds().get(0)) :
                empty();
    }

    private boolean messageHasFooter(Message message) {
        if (message.getEmbeds().size() == 0) {
            return false;
        }
        Embed embed = message.getEmbeds().get(0);
        return embed.getFooter().map(footer -> footer.getText().isPresent()).orElse(false);
    }


    private Optional<String> getSourceChangeReaction(Embed embed) {
        if (!embed.getFooter().map(footer -> footer.getText().isPresent()).orElse(false)) {
            return empty();
        }
        String footerPart = asList(embed.getFooter().get().getText().get().split(" \\| ")).get(2);
        if (!footerPart.contains("rules available")) {
            return empty();
        }
        return footerPart.split(" ")[0].equals("paper") ? Optional.of(PAPER_EMOJI) : Optional.of(DIGITAL_EMOJI);
    }
}
