package service.interaction_pagination;

import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.LowLevelComponent;

public class InteractionPaginationStatics {

    public static final String LEFT_PAGINATION_STRING = "page_left";
    public static final String RIGHT_PAGINATION_STRING = "page_right";
    public static final String DELETE_STRING = "delete";
    public static final String SWAP_SOURCE_STRING = "change_rule_source";
    public static final String WEBSITE_LINK_STRING = "to_website";

    public static final LowLevelComponent LEFT_PAGINATION_COMPONENT = Button.primary(LEFT_PAGINATION_STRING, "Previous page");
    public static final LowLevelComponent RIGHT_PAGINATION_COMPONENT = Button.primary(RIGHT_PAGINATION_STRING, "Next page");
    public static final LowLevelComponent DELETE_COMPONENT = Button.danger(DELETE_STRING, "Delete");
    public static final LowLevelComponent SWAP_SOURCE_COMPONENT = Button.secondary(SWAP_SOURCE_STRING, "Swap digital/paper");
    public static final LowLevelComponent WEBSITE_LINK_COMPONENT = Button.link(WEBSITE_LINK_STRING, "Browse");
}
