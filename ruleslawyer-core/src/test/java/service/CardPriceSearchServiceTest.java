package service;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class CardPriceSearchServiceTest {
    private static final String TEST_URL = "https://api.scryfall.com/cards/8c39f9b4-02b9-4d44-b8d6-4fd02ebbb0c5";

    private CardPriceSearchService cardPriceSearchService;

    @Before
    public void setUp() {
        this.cardPriceSearchService = new CardPriceSearchService();
    }

    @Test
    public void testStuff() throws IOException {
        Map<String, String> elem = cardPriceSearchService.getPrice(TEST_URL);
        assertThat(elem.size(), is(6));
    }
}
