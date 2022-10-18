package ingestion.card;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import contract.cards.Card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;

public class JsonCardIngestionService {

    public static List<Card> getCards() {
        try {
            InputStream in = JsonCardIngestionService.class.getResourceAsStream("/cards.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("Windows-1252")));
            char[] buffer = new char[90000000];
            br.read(buffer);
            in.close();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(valueOf(buffer), new TypeReference<List<Card>>() {});
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        return emptyList();
    }
}
