package search.interaction_pagination;

import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import search.DiscordRuleSearchService;
import search.contract.DiscordReturnPayload;
import search.contract.request.DiscordRuleSearchRequest;
import search.contract.request.builder.DiscordSearchRequestBuilder;
import search.interaction_pagination.pagination_enum.PageDirection;

import java.util.List;
import java.util.Optional;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static search.contract.request.builder.DiscordSearchRequestBuilder.aDiscordSearchRequest;
import static search.interaction_pagination.InteractionPaginationStatics.LEFT_PAGINATION_STRING;
import static search.interaction_pagination.InteractionPaginationStatics.RIGHT_PAGINATION_STRING;
import static search.interaction_pagination.pagination_enum.PageDirection.*;
import static search.interaction_pagination.pagination_enum.PageDirection.TO_DIGITAL;

public class RulePaginationService {

    private DiscordRuleSearchService discordRuleSearchService;

    public RulePaginationService(DiscordRuleSearchService discordRuleSearchService) {
        this.discordRuleSearchService = discordRuleSearchService;
    }

    protected Optional<DiscordReturnPayload> paginateRules(MessageComponentCreateEvent event) {
        Message message = event.getMessageComponentInteraction().getMessage();
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
        discordSearchRequest.setPageNumber(parseInt(asList(footerParts.get(0).split(" ")).get(1))-1);
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
}
