package app.slack.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SlackField {

    @JsonProperty("type")
    private String type;

    @JsonProperty("text")
    private String text;

    public SlackField(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
