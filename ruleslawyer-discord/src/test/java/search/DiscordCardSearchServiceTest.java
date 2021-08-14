package search;

import contract.searchRequests.CardSearchRequestType;
import init_utils.ManaEmojiService;
import org.javacord.api.DiscordApiBuilder;
import org.junit.Before;
import org.junit.Test;
import search.contract.DiscordReturnPayload;
import search.contract.request.DiscordCardSearchRequest;

import static contract.cards.FormatLegality.ANY_FORMAT;
import static contract.searchRequests.CardSearchRequestType.INCLUDE_ORACLE;
import static java.util.Collections.singletonList;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.ORACLE;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.RULINGS;
import static search.interaction_pagination.pagination_enum.CardPageDirection.NEXT_CARD;
import static utils.DiscordUtils.getDiscordKey;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DiscordCardSearchServiceTest {

    private ManaEmojiService manaEmojiService;
    private DiscordCardSearchService discordCardSearchService;

    @Before
    public void setUp() {
        manaEmojiService = new ManaEmojiService(new DiscordApiBuilder()
                .setToken(getDiscordKey("dev"))
                .login()
                .join()
        );
        discordCardSearchService = new DiscordCardSearchService(manaEmojiService);
    }

    @Test
    public void searchForCard_NoCardExists_VerifyReturnValue() {
        DiscordReturnPayload output = discordCardSearchService.getSearchResult("Elaine", "Blue Eyes White Dragon", ORACLE);

        assertThat(output.getComponents().length, is(1));
        assertThat(output.getEmbed().getFooter().length(), is(0));
        assertThat(output.getEmbed().getTitle(), is("No card found"));
    }

    @Test
    public void searchForCardWithRequest_SearchForCardWithKeywords_VerifySame() {
        DiscordCardSearchRequest searchRequest = new DiscordCardSearchRequest(
                singletonList("cryptic"),
                ANY_FORMAT,
                "Elaine",
                ORACLE,
                INCLUDE_ORACLE,
                1
        );

        DiscordReturnPayload firstReturnValue = discordCardSearchService.getSearchResult(searchRequest);
        DiscordReturnPayload secondReturnValue = discordCardSearchService.getSearchResult("Elaine", "cryptic", ORACLE);

        assertThat(firstReturnValue, is(secondReturnValue));
    }

    @Test
    public void searchForCard_Exists_VerifyReturnValue_PaginateReturnType_Verify_PaginateNextCard_Verify() {
        DiscordCardSearchRequest searchRequest = new DiscordCardSearchRequest(
                singletonList("thalia"),
                ANY_FORMAT,
                "Elaine",
                ORACLE,
                INCLUDE_ORACLE,
                1
        );

        DiscordReturnPayload firstOutput = discordCardSearchService.getSearchResult(searchRequest);

        assertThat(firstOutput.getComponents().length, is(3));
        assertThat(firstOutput.getEmbed().getFooter(), is("Elaine | thalia | oracle | Page 1"));
        assertThat(firstOutput.getEmbed().getTitle().startsWith("Thalia, Guardian of Thraben"), is(true));

        searchRequest.setCardDataReturnType(RULINGS);

        DiscordReturnPayload rulingsPageOutput = discordCardSearchService.getSearchResult(searchRequest);

        assertThat(rulingsPageOutput.getEmbed().getFooter(), is("Elaine | thalia | rulings | Page 1"));
        assertThat(rulingsPageOutput.getEmbed().getTitle(), is("Thalia, Guardian of Thraben"));
        assertThat(rulingsPageOutput.getEmbed().getFields().get(0).getFieldName(), is("Ruling"));

        searchRequest.paginateSearchRequest(NEXT_CARD);

        DiscordReturnPayload paginatedOutput = discordCardSearchService.getSearchResult(searchRequest);

        assertThat(paginatedOutput.getEmbed().getFooter(), is("Elaine | thalia | rulings | Page 2"));
        assertThat(paginatedOutput.getEmbed().getTitle(), is("Thalia, Heretic Cathar"));
    }
}
