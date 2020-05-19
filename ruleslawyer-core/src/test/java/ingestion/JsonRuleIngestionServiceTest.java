package ingestion;

import contract.rules.AbstractRule;
import ingestion.rule.JsonRuleIngestionService;
import org.junit.Test;

import java.util.List;

public class JsonRuleIngestionServiceTest {

    @Test
    public void doStuff() {
        List<AbstractRule> rules = JsonRuleIngestionService.getRules();

        System.out.println(rules.size());
    }
}
