package service.reaction_pagination;

import contract.RuleSource;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import search.SearchService;
import search.contract.DiscordSearchRequest;
import search.contract.DiscordSearchResult;


import java.util.List;
import java.util.Optional;

import static com.vdurmont.emoji.EmojiParser.parseToUnicode;
import static contract.RuleSource.ANY;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static service.reaction_pagination.PageDirection.NEXT_PAGE;
import static service.reaction_pagination.PageDirection.PREVIOUS_PAGE;
import static utils.DiscordUtils.*;

public class ReactionPaginationService {

    public static final String LEFT_EMOJI = parseToUnicode(":arrow_left:");
    public static final String RIGHT_EMOJI = parseToUnicode(":arrow_right:");

    private SearchService searchService;

    public ReactionPaginationService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void handleReactionPaginationEvent(ReactionAddEvent event) {
        Optional<PageDirection> pageDirection = getPaginationDirection(event);
        if (isOwnReaction(event) || !isOwnMessage(event) || !pageDirection.isPresent())
            return;
        Embed embed = event.getMessage().get().getEmbeds().get(0);
        DiscordSearchRequest searchRequest = getSearchRequestFromEmbed(embed.getTitle().get(), embed.getFooter().get().getText().get());

        if (searchRequest.getRequester().equals(getUsernameForReactionAddEvent(event).get())) {
            searchRequest.getNextPage(pageDirection.get());
            DiscordSearchResult result = searchService.getSearchResult(searchRequest);
            event.getMessage().get().edit(result.getEmbed());
            event.removeReaction();
        }
    }

    private Optional<PageDirection> getPaginationDirection(ReactionAddEvent event) {
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
        return new DiscordSearchRequest(requester, keywords, ruleSource, pageNumber, false);
    }

    public boolean shouldPaginate(MessageCreateEvent event) {
        return isOwnMessage(event) && event.getMessage().getEmbeds().size() == 1
                && event.getMessage().getEmbeds().get(0).getTitle().isPresent();
    }
}
