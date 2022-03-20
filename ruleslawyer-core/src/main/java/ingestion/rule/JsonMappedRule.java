package ingestion.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class JsonMappedRule {

    private String text;
    private List<JsonMappedRule> subRules;
    private List<String> citations;

    public JsonMappedRule(
            @JsonProperty("text") String text,
            @JsonProperty("subRules") List<JsonMappedRule> subRules,
            @JsonProperty("keywords") List<String> citations
    ) {
        this.text = text.replace(" EOL ", "\n");
        this.subRules = subRules;
        this.citations = citations;
    }

    public String getText() {
        return text;
    }

    public List<JsonMappedRule> getSubRules() {
        return subRules;
    }

    public List<String> getCitations() {
        return this.citations;
    }

    @Override
    public String toString() {
        if (subRules != null)
            return this.text + "\n" + subRules.stream().map(JsonMappedRule::toString).collect(Collectors.joining("\n"));
        return this.text;
    }
}
