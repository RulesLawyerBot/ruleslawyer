package repository;

import contract.searchResults.SearchResult;
import contract.cards.Card;
import contract.searchRequests.CardSearchRequest;
import org.junit.Test;
import utils.CardTestUtils;

import java.util.List;

import static contract.cards.GameFormat.ANY_FORMAT;
import static contract.searchRequests.CardSearchRequestType.INCLUDE_ORACLE;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CardSearchRepositoryTest {

    SearchRepository<Card> repository = new SearchRepository<>(CardTestUtils.getSearchSpace());

    @Test
    public void findWithKeyword_ExpectCardsFound() {
        CardSearchRequest searchRequest = new CardSearchRequest(singletonList("noncreature"), ANY_FORMAT, 1, INCLUDE_ORACLE);

        List<SearchResult<Card>> result = repository.getSearchResult(searchRequest);

        assertThat(result.size(), is(2));
    }
}
