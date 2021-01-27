package service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import search.SearchService;
import search.contract.DiscordSearchRequest;
import service.reaction_pagination.ReactionPaginationService;

import static contract.RuleSource.ANY;
import static contract.RuleSource.CR;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReactionPaginationServiceTest {

    @Mock
    private SearchService searchService;
    private MessageLoggingService messageLoggingService;

    private ReactionPaginationService reactionPaginationService;

    @Before
    public void setUp() {
        this.reactionPaginationService = new ReactionPaginationService(searchService, messageLoggingService);
    }

    @Test
    public void ParseEmbeddedRequest() {
        DiscordSearchRequest nonspecificSearchRequest = reactionPaginationService.getSearchRequestFromEmbed("mana/symbol", "Requested by: Elaine | page 1 of 10 | Use arrow keys for pagination");
        DiscordSearchRequest expectedNonspecificResult = new DiscordSearchRequest("Elaine", asList("mana", "symbol"), ANY, 1, false);
        assertThat(nonspecificSearchRequest, is(expectedNonspecificResult));

        DiscordSearchRequest specificSearchRequest = reactionPaginationService.getSearchRequestFromEmbed("CR | mana/symbol\n", "Requested by: Elaine | page 1 of 10 | Use arrow keys for pagination");
        DiscordSearchRequest expectedSpecificResult = new DiscordSearchRequest("Elaine", asList("mana", "symbol"), CR, 1, false);
        assertThat(specificSearchRequest, is(expectedSpecificResult));
    }
}
