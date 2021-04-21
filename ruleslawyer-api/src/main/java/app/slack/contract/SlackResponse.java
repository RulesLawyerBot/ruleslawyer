package app.slack.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SlackResponse {

    @JsonProperty("response_type")
    private String responseType;

    @JsonProperty("blocks")
    private List<SlackBlock> blocks;

    @JsonProperty("attachments")
    private List<SlackAttachment> attachments;

    public SlackResponse(String responseType, List<SlackBlock> blocks, List<SlackAttachment> attachments) {
        this.responseType = responseType;
        this.blocks = blocks;
        this.attachments = attachments;
    }

    public String getResponseType() {
        return responseType;
    }

    public List<SlackBlock> getBlocks() {
        return blocks;
    }

    public List<SlackAttachment> getAttachments() {
        return attachments;
    }
}
