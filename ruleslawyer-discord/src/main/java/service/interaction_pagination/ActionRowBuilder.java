package service.interaction_pagination;

import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.LowLevelComponent;

import java.util.ArrayList;
import java.util.List;

import static service.interaction_pagination.InteractionPaginationStatics.*;

public class ActionRowBuilder {

    private boolean hasPages;
    private boolean hasSourceSwap;
    private boolean hasDelete;
    private boolean hasLink;

    public ActionRowBuilder () {
        hasPages = false;
        hasSourceSwap = false;
        hasDelete = false;
        hasLink = false;
    }

    public ActionRowBuilder setHasPages() {
        this.hasPages = true;
        return this;
    }

    public ActionRowBuilder setHasSourceSwap() {
        this.hasSourceSwap = true;
        return this;
    }

    public ActionRowBuilder setHasDelete() {
        this.hasDelete = true;
        return this;
    }

    public ActionRowBuilder setHasLink() { //TODO
        this.hasLink = true;
        return this;
    }

    public List<LowLevelComponent> build() {
        List<LowLevelComponent> output = new ArrayList<>();
        if (hasPages) {
            output.add(LEFT_PAGINATION_COMPONENT);
            output.add(RIGHT_PAGINATION_COMPONENT);
        }
        if (hasSourceSwap) {
            output.add(SWAP_SOURCE_COMPONENT);
        }
        if (hasDelete) {
            output.add(DELETE_COMPONENT);
        }
        if (hasLink) {
            output.add(WEBSITE_LINK_COMPONENT);
        }
        return output;
    }

    public ActionRow buildToRow() {
        return ActionRow.of(build());
    }
}
