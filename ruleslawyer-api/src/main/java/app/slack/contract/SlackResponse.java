package app.slack.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SlackResponse {

    @JsonProperty("response_type")
    private String responseType;

    @JsonProperty("blocks")
    private List<SlackBlock> blocks;

    public SlackResponse(String responseType, List<SlackBlock> blocks) {
        this.responseType = responseType;
        this.blocks = blocks;
    }

    public String getResponseType() {
        return responseType;
    }

    public List<SlackBlock> getBlocks() {
        return blocks;
    }
}
