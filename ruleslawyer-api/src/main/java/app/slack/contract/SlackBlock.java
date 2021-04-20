package app.slack.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SlackBlock {

    @JsonProperty("type")
    private String type;

    @JsonProperty("fields")
    private List<SlackField> fields;

    public SlackBlock(String type, List<SlackField> fields) {
        this.type = type;
        this.fields = fields;
    }

    public String getType() {
        return type;
    }

    public List<SlackField> getFields() {
        return fields;
    }
}
