package app.slack.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SlackAttachment {

    @JsonProperty("blocks")
    List<SlackBlock> blocks;

    public SlackAttachment(List<SlackBlock> blocks) {
        this.blocks = blocks;
    }

    public List<SlackBlock> getBlocks() {
        return blocks;
    }
}
