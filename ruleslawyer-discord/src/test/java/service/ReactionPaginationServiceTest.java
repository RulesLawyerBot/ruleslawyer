package service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import search.DiscordRuleSearchService;
import search.contract.request.DiscordRuleSearchRequest;
import service.reaction_pagination.ReactionPaginationService;

import static contract.rules.enums.RuleRequestCategory.ANY_RULE_TYPE;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static contract.rules.enums.RuleSource.CR;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Deprecated
@Ignore
public class ReactionPaginationServiceTest {

    @Mock
    private DiscordRuleSearchService searchService;

    @Mock
    private MessageLoggingService messageLoggingService;

    private ReactionPaginationService reactionPaginationService;

    @Before
    public void setUp() {
        this.reactionPaginationService = new ReactionPaginationService(searchService, messageLoggingService);
    }

    @Test
    public void ParseEmbeddedRequest() {
        DiscordRuleSearchRequest nonspecificSearchRequest = reactionPaginationService.getSearchRequestFromEmbed("mana/symbol", "Requested by: Elaine | page 1 of 10 | Use arrow reactions for pagination");
        DiscordRuleSearchRequest expectedNonspecificResult = new DiscordRuleSearchRequest("Elaine", asList("mana", "symbol"), ANY_DOCUMENT, 1, ANY_RULE_TYPE);
        assertThat(nonspecificSearchRequest, is(expectedNonspecificResult));

        DiscordRuleSearchRequest specificSearchRequest = reactionPaginationService.getSearchRequestFromEmbed("CR | mana/symbol\n", "Requested by: Elaine | page 1 of 10 | Use arrow reactions for pagination");
        DiscordRuleSearchRequest expectedSpecificResult = new DiscordRuleSearchRequest("Elaine", asList("mana", "symbol"), CR, 1, ANY_RULE_TYPE);
        assertThat(specificSearchRequest, is(expectedSpecificResult));
    }
}
