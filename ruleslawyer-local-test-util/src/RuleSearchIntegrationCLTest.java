import contract.searchResults.SearchResult;
import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import ingestion.rule.JsonRuleIngestionService;
import repository.SearchRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static contract.RuleSource.ANY;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

public class RuleSearchIntegrationCLTest {

    public static void main(String[] args) throws IOException {
        JsonRuleIngestionService jsonRuleIngestionService = new JsonRuleIngestionService();
        List<AbstractRule> searchSpace = jsonRuleIngestionService.getRules();
        SearchRepository<AbstractRule> repository = new SearchRepository<>(searchSpace);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String input = reader.readLine();
            RuleSearchRequest request = new RuleSearchRequest(asList(input.split(" ")), ANY, 0, false);
            List<SearchResult<AbstractRule>> results = repository.getSearchResult(request);
            String output = results.stream()
                    .map(SearchResult::getEntry)
                    .map(AbstractRule::getText)
                    .limit(20)
                    .collect(joining("\n"));
            System.out.println(output);
        }
    }
}
