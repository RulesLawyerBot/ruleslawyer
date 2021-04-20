package ingestion;

import contract.rules.AbstractRule;
import ingestion.rule.JsonRuleIngestionService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JsonRuleIngestionServiceTest {

    JsonRuleIngestionService jsonRuleIngestionService;

    @Before
    public void setUp() {
        jsonRuleIngestionService = new JsonRuleIngestionService();
    }

    @Test
    public void doStuff() {
        List<AbstractRule> rules = jsonRuleIngestionService.getRawRulesData();

        System.out.println(rules.size());
    }
}
