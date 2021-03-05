package service.reaction_pagination;

import contract.RuleSource;
import contract.searchRequests.SearchRequest;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;
import search.SearchService;
import search.contract.DiscordSearchRequest;
import search.contract.DiscordSearchResult;
import service.MessageLoggingService;


import java.util.List;
import java.util.Optional;

import static com.vdurmont.emoji.EmojiParser.parseToUnicode;
import static contract.RuleSource.ANY;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static search.contract.builder.DiscordSearchRequestBuilder.aDiscordSearchRequest;
import static service.reaction_pagination.PageDirection.NEXT_PAGE;
import static service.reaction_pagination.PageDirection.PREVIOUS_PAGE;
import static utils.DiscordUtils.*;

public class ReactionPaginationService {

    public static final String LEFT_EMOJI = parseToUnicode(":arrow_left:");
    public static final String RIGHT_EMOJI = parseToUnicode(":arrow_right:");

    private SearchService searchService;
    private MessageLoggingService messageLoggingService;

    public ReactionPaginationService(SearchService searchService, MessageLoggingService messageLoggingService) {
        this.searchService = searchService;
        this.messageLoggingService = messageLoggingService;
    }

    public void handleReactionPaginationEvent(SingleReactionEvent event) {
        Optional<PageDirection> pageDirection = getPaginationDirection(event);
        if (isOwnReaction(event) || !isOwnMessage(event) || !pageDirection.isPresent())
            return;
        Embed embed = event.getMessage().get().getEmbeds().get(0);
        DiscordSearchRequest searchRequest = getSearchRequestFromEmbed(embed.getTitle().get(), embed.getFooter().get().getText().get());

        if (hasPaginationPermissions(searchRequest, event)) {
            searchRequest.getNextPage(pageDirection.get());
            DiscordSearchResult result = searchService.getSearchResult(searchRequest);
            messageLoggingService.logEditInput(pageDirection.get(), embed);
            messageLoggingService.logOutput(result.getEmbed());
            event.getMessage().get().edit(result.getEmbed());
            if (event instanceof ReactionAddEvent) {
                ((ReactionAddEvent)event).removeReaction();
            }
        }
    }

    private Optional<PageDirection> getPaginationDirection(SingleReactionEvent event) {
        if (isOwnReaction(event) || !isOwnMessage(event)) {
            return empty();
        }
        if (event.getEmoji().equalsEmoji(LEFT_EMOJI))
            return Optional.of(PREVIOUS_PAGE);
        if (event.getEmoji().equalsEmoji(RIGHT_EMOJI))
            return Optional.of(NEXT_PAGE);
        return empty();
    }

    public DiscordSearchRequest getSearchRequestFromEmbed(String header, String footer) {
        List<String> headerParts = asList(header.split(" \\| "));
        RuleSource ruleSource = headerParts.size() > 1 ? RuleSource.valueOf(headerParts.get(0)) : ANY;
        List<String> keywords = asList(headerParts.get(headerParts.size()-1).split("/"));

        List<String> footerParts = asList(footer.split(" \\| "));
        String requester = footerParts.get(0).substring("Requested by: ".length());
        Integer pageNumber = parseInt(asList(footerParts.get(1).split(" ")).get(1))-1;
        return aDiscordSearchRequest()
                .setRequester(requester)
                .appendKeywords(keywords)
                .setRuleSource(ruleSource)
                .setPageNumber(pageNumber)
                .setDigitalRuleRequest(false)
                .build();
    }

    private boolean hasPaginationPermissions(DiscordSearchRequest searchRequest, SingleReactionEvent event) {
        if (event.getUser().map(
                user -> event.getServer().isPresent()
                        && event.getServer().get().canBanUsers(user)
                ).orElse(false)
        ) {
            return true;
        }
        return event.getUser().map(
                user -> user.getDiscriminatedName().equals(searchRequest.getRequester())
        ).orElse(false);
    }

    public boolean shouldPlacePaginationReactions(MessageCreateEvent event) {
        return isOwnMessage(event) && event.getMessage().getEmbeds().size() == 1
                && event.getMessage().getEmbeds().get(0).getTitle().isPresent();
    }
}
