package service.interaction_pagination;

import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.LowLevelComponent;

public class InteractionPaginationStatics {

    public static final String LEFT_PAGINATION_STRING = "page_left";
    public static final String RIGHT_PAGINATION_STRING = "page_right";
    public static final String SWAP_SOURCE_STRING = "change_rule_source";

    public static final String ORACLE_TEXT_STRING = "card_oracle";
    public static final String RULINGS_TEXT_STRING = "card_rulings";
    public static final String LEGALITY_STRING = "card_legality";
    public static final String CARD_ART_STRING = "card_art";

    public static final String DELETE_STRING = "delete";
    public static final String WEBSITE_LINK_STRING = "to_website";

    public static final LowLevelComponent LEFT_PAGINATION_COMPONENT = Button.primary(LEFT_PAGINATION_STRING, "Previous page");
    public static final LowLevelComponent RIGHT_PAGINATION_COMPONENT = Button.primary(RIGHT_PAGINATION_STRING, "Next page");
    public static final LowLevelComponent SWAP_SOURCE_COMPONENT = Button.secondary(SWAP_SOURCE_STRING, "Swap digital/paper");

    public static final LowLevelComponent ORACLE_TEXT_COMPONENT = Button.secondary(ORACLE_TEXT_STRING, "Oracle");
    public static final LowLevelComponent RULINGS_TEXT_COMPONENT = Button.secondary(RULINGS_TEXT_STRING, "Card rulings");
    public static final LowLevelComponent LEGALITY_COMPONENT = Button.secondary(LEGALITY_STRING, "Card legality");
    public static final LowLevelComponent CARD_ART_COMPONENT = Button.secondary(CARD_ART_STRING, "Full card art");

    public static final LowLevelComponent DELETE_COMPONENT = Button.danger(DELETE_STRING, "Delete");
    public static final LowLevelComponent WEBSITE_LINK_COMPONENT = Button.link(WEBSITE_LINK_STRING, "Browse");

    public static final ActionRow DELETE_ONLY_ROW = ActionRow.of(DELETE_COMPONENT);
    public static final ActionRow RULE_ROW_WITHOUT_SOURCE_SWAP = ActionRow.of(LEFT_PAGINATION_COMPONENT, RIGHT_PAGINATION_COMPONENT, DELETE_COMPONENT);
    public static final ActionRow RULE_ROW_WITH_SOURCE_SWAP = ActionRow.of(LEFT_PAGINATION_COMPONENT, RIGHT_PAGINATION_COMPONENT, SWAP_SOURCE_COMPONENT, DELETE_COMPONENT);
    public static final ActionRow CARD_ROW = ActionRow.of(ORACLE_TEXT_COMPONENT, RULINGS_TEXT_COMPONENT, LEGALITY_COMPONENT, CARD_ART_COMPONENT);
}
