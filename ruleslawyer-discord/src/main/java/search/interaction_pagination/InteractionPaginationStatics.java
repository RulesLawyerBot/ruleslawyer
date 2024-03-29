package search.interaction_pagination;

import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.LowLevelComponent;

import static java.lang.String.valueOf;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.*;
import static search.interaction_pagination.pagination_enum.CardPageDirection.NEXT_CARD;
import static search.interaction_pagination.pagination_enum.CardPageDirection.PREVIOUS_CARD;

public class InteractionPaginationStatics {

    public static final String LEFT_PAGINATION_STRING = "page_left";
    public static final String RIGHT_PAGINATION_STRING = "page_right";
    public static final String SWAP_SOURCE_STRING = "change_rule_source";

    public static final String DELETE_STRING = "delete";
    public static final String WEBSITE_LINK_STRING = "to_website";

    public static final LowLevelComponent LEFT_PAGINATION_COMPONENT = Button.primary(LEFT_PAGINATION_STRING, "Previous page");
    public static final LowLevelComponent RIGHT_PAGINATION_COMPONENT = Button.primary(RIGHT_PAGINATION_STRING, "Next page");
    public static final LowLevelComponent SWAP_SOURCE_COMPONENT = Button.secondary(SWAP_SOURCE_STRING, "Swap digital/paper");

    public static final LowLevelComponent ORACLE_TEXT_COMPONENT = Button.secondary(valueOf(ORACLE), "Oracle");
    public static final LowLevelComponent RULINGS_TEXT_COMPONENT = Button.secondary(valueOf(RULINGS), "Card rulings");
    public static final LowLevelComponent LEGALITY_COMPONENT = Button.secondary(valueOf(LEGALITY), "Card legality");
    public static final LowLevelComponent CARD_ART_COMPONENT = Button.secondary(valueOf(ART), "Full card art");
    public static final LowLevelComponent PRICE_COMPONENT = Button.secondary(valueOf(PRICE), "Price");

    public static final LowLevelComponent PREVIOUS_CARD_COMPONENT = Button.primary(valueOf(PREVIOUS_CARD), "Previous card");
    public static final LowLevelComponent NEXT_CARD_COMPONENT = Button.primary(valueOf(NEXT_CARD), "Next card");

    public static final LowLevelComponent DELETE_COMPONENT = Button.danger(DELETE_STRING, "Delete");

    public static final ActionRow DELETE_ONLY_ROW = ActionRow.of(DELETE_COMPONENT);
    public static final ActionRow RULE_ROW_WITHOUT_SOURCE_SWAP = ActionRow.of(LEFT_PAGINATION_COMPONENT, RIGHT_PAGINATION_COMPONENT, DELETE_COMPONENT);
    public static final ActionRow RULE_ROW_WITH_SOURCE_SWAP = ActionRow.of(LEFT_PAGINATION_COMPONENT, RIGHT_PAGINATION_COMPONENT, SWAP_SOURCE_COMPONENT, DELETE_COMPONENT);
    public static final ActionRow CARD_ROW = ActionRow.of(ORACLE_TEXT_COMPONENT, RULINGS_TEXT_COMPONENT, LEGALITY_COMPONENT, CARD_ART_COMPONENT, PRICE_COMPONENT);
    public static final ActionRow CARD_PAGINATION_ROW = ActionRow.of(PREVIOUS_CARD_COMPONENT, NEXT_CARD_COMPONENT, DELETE_COMPONENT);
}
