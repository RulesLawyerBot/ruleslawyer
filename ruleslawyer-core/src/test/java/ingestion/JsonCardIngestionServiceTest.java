package ingestion;

import contract.cards.Card;
import ingestion.card.JsonCardIngestionService;
import org.junit.Test;

import java.util.List;

public class JsonCardIngestionServiceTest {

    @Test
    public void doStuff() {
        List<Card> cards = JsonCardIngestionService.getCards();
        System.out.println(cards.size());
    }
}
