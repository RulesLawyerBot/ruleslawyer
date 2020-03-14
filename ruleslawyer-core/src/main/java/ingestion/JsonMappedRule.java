package ingestion;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class JsonMappedRule {

    private String text;
    private List<JsonMappedRule> subRules;

    public JsonMappedRule(@JsonProperty("text") String text, @JsonProperty("subRules") List<JsonMappedRule> subRules) {
        this.text = text.replace(" EOL ", "\n");
        this.subRules = subRules;
    }

    public String getText() {
        return text;
    }

    public List<JsonMappedRule> getSubRules() {
        return subRules;
    }

    @Override
    public String toString() {
        if (subRules != null)
            return this.text + "\n" + subRules.stream().map(JsonMappedRule::toString).collect(Collectors.joining("\n"));
        return this.text;
    }
}
