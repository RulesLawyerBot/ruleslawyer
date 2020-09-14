package repository;

import contract.searchResults.SearchResult;
import contract.cards.Card;
import contract.searchRequests.CardSearchRequest;
import org.junit.Test;
import utils.CardTestUtils;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CardSearchRepositoryTest {

    SearchRepository<Card> repository = new SearchRepository<>(CardTestUtils.getSearchSpace());

    @Test
    public void findWithKeyword_ExpectCardsFound() {
        CardSearchRequest searchRequest = new CardSearchRequest(singletonList("noncreature"));

        List<SearchResult<Card>> result = repository.getSearchResult(searchRequest);

        assertThat(result.size(), is(2));
    }
}
