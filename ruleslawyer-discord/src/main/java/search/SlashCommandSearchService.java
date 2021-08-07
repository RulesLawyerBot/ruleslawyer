package search;

import exception.NotYetImplementedException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import search.contract.DiscordReturnPayload;
import service.HelpMessageSearchService;
import search.interaction_pagination.pagination_enum.CardDataReturnType;

import java.util.List;

import static contract.rules.enums.RuleSource.*;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.javacord.api.interaction.SlashCommandOption.*;
import static org.javacord.api.interaction.SlashCommandOptionType.STRING;
import static service.HelpMessageSearchService.*;
import static search.interaction_pagination.InteractionPaginationStatics.*;
import static search.interaction_pagination.pagination_enum.CardDataReturnType.*;

public class SlashCommandSearchService {

    public static final String RULE_SLASH_COMMAND_IDENTIFIER = "rule";
    public static final String CARD_SLASH_COMMAND_IDENTIFIER = "card";
    public static final String HELP_SLASH_COMMAND_IDENTIFIER = "help";

    private DiscordApi api;
    private DiscordRuleSearchService discordRuleSearchService;
    private DiscordCardSearchService discordCardSearchService;
    private HelpMessageSearchService helpMessageSearchService;

    public SlashCommandSearchService(DiscordApi api, DiscordRuleSearchService discordRuleSearchService, DiscordCardSearchService discordCardSearchService) {
        this.api = api;
        this.discordRuleSearchService = discordRuleSearchService;
        this.discordCardSearchService = discordCardSearchService;
        this.helpMessageSearchService = new HelpMessageSearchService();
    }

    public void setCommands() {
        List<SlashCommand> commands = api.getGlobalSlashCommands().join();
        commands.forEach(SlashCommand::deleteGlobal);
        SlashCommand.with(
                RULE_SLASH_COMMAND_IDENTIFIER,
                "search RulesLawyer for a rule",
                asList(
                        create(
                                STRING,
                                "query",
                                "Query parameters (surround with \"quotes\" for exact match) - tab twice for additional parameters",
                                true
                        ),
                        createWithChoices(
                                STRING,
                                "rule_source",
                                "Skip this argument to search everything, or pick a source document",
                                false,
                                asList(
                                        SlashCommandOptionChoice.create("Any document (default)", valueOf(ANY_DOCUMENT)),
                                        SlashCommandOptionChoice.create("Comprehensive Rules", valueOf(CR)),
                                        SlashCommandOptionChoice.create("Infraction Procedure Guide", valueOf(IPG)),
                                        SlashCommandOptionChoice.create("Magic Tournament Rules", valueOf(MTR)),
                                        SlashCommandOptionChoice.create("Judging At Regular", valueOf(JAR)),
                                        SlashCommandOptionChoice.create("Oathbreaker", valueOf(OATH)),
                                        SlashCommandOptionChoice.create("Digital Infraction Procedure Guide", valueOf(DIPG)),
                                        SlashCommandOptionChoice.create("Digital Magic Tournament Rules", valueOf(DMTR))
                                )
                        )
                )
        )
                .createGlobal(api)
                .join();
        SlashCommand.with(
                CARD_SLASH_COMMAND_IDENTIFIER,
                "search RulesLawyer for a card",
                asList(
                        create(
                                STRING,
                                "card_name",
                                "Search card name or oracle - tab twice for additional parameters",
                                true
                        ),
                        createWithChoices(
                                STRING,
                                "Options",
                                "Skip this argument for oracle, or \"oracle\" \"rulings\" \"legality\" \"art\" \"price\"",
                                false,
                                asList(
                                        SlashCommandOptionChoice.create("Oracle: Oracle text for this card", valueOf(ORACLE)),
                                        SlashCommandOptionChoice.create("Rulings: Oracle rulings on this card", valueOf(RULINGS)),
                                        SlashCommandOptionChoice.create("Legality: What formats this card is legal in", valueOf(LEGALITY)),
                                        SlashCommandOptionChoice.create("Art: Full art of the card", valueOf(ART)),
                                        SlashCommandOptionChoice.create("Price: prices in USD, EUR, and TIX", valueOf(PRICE))
                                )
                        )
                )
        )
                .createGlobal(api)
                .join();
        SlashCommand.with(
                HELP_SLASH_COMMAND_IDENTIFIER,
                "RulesLawyer help files (press tab for a list of optional arguments)",
                singletonList(
                        createWithChoices(
                                STRING,
                                "query",
                                "Blank for main help, or \"add\" \"dev\" \"about\"",
                                false,
                                asList(
                                        SlashCommandOptionChoice.create("main: main help file", HELP_MAIN_IDENTIFIER),
                                        SlashCommandOptionChoice.create("add: how to add this to your server", HELP_ADD_IDENTIFIER),
                                        SlashCommandOptionChoice.create("dev: patch notes", HELP_DEV_IDENTIFIER),
                                        SlashCommandOptionChoice.create("about: about the author", HELP_ABOUT_IDENTIFIER)
                                )
                        )
                )
        )
                .createGlobal(api)
                .join();
    }

    public void respondToSlashCommand(SlashCommandCreateEvent event) {
        String commandName = event.getSlashCommandInteraction().getCommandName();
        if (commandName.equals(RULE_SLASH_COMMAND_IDENTIFIER)) {
            respondToRuleCommand(event);
        } else if (commandName.equals(CARD_SLASH_COMMAND_IDENTIFIER)) {
            respondToCardCommand(event);
        } else if (commandName.equals(HELP_SLASH_COMMAND_IDENTIFIER)) {
            respondToHelpCommand(event);
        } else {
            throw new NotYetImplementedException();
        }
    }

    public void respondToRuleCommand(SlashCommandCreateEvent event) {
        DiscordReturnPayload searchResult =
                discordRuleSearchService.getSearchResultFromPlainQuery(
                        event.getSlashCommandInteraction().getUser().getDiscriminatedName(),
                        event.getSlashCommandInteraction().getFirstOptionStringValue().orElse("") + "|" + event.getSlashCommandInteraction().getSecondOptionStringValue().orElse("")
                );
        event.getSlashCommandInteraction().createImmediateResponder()
                .setContent(searchResult.getContent())
                .addEmbed(searchResult.getEmbed())
                .addComponents(searchResult.getComponents())
                .respond();
    }

    private void respondToCardCommand(SlashCommandCreateEvent event) {
        CardDataReturnType cardDataReturnType = event.getSlashCommandInteraction().getSecondOptionStringValue().map(CardDataReturnType::valueOf).orElse(ORACLE);
        if (cardDataReturnType == PRICE) {
            event.getSlashCommandInteraction().respondLater();
            EmbedBuilder embed = discordCardSearchService.getSearchResult(
                    event.getSlashCommandInteraction().getUser().getDiscriminatedName(),
                    event.getSlashCommandInteraction().getFirstOptionStringValue().orElse(""),
                    cardDataReturnType
            );
            event.getSlashCommandInteraction().createFollowupMessageBuilder()
                    .addEmbed(embed)
                    .addComponents(CARD_ROW, CARD_PAGINATION_ROW)
                    .send();
        } else {
            EmbedBuilder embed = discordCardSearchService.getSearchResult(
                    event.getSlashCommandInteraction().getUser().getDiscriminatedName(),
                    event.getSlashCommandInteraction().getFirstOptionStringValue().orElse(""),
                    cardDataReturnType
            );
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(embed)
                    .addComponents(CARD_ROW, CARD_PAGINATION_ROW)
                    .respond();
        }
    }

    private void respondToHelpCommand(SlashCommandCreateEvent event) {
        EmbedBuilder helpFile = event.getSlashCommandInteraction()
                .getFirstOptionStringValue()
                .map(value -> helpMessageSearchService.getHelpFile(value))
                .orElse(helpMessageSearchService.getHelpFile());
        event.getSlashCommandInteraction()
                .createImmediateResponder()
                .addEmbed(helpFile)
                .addComponents(DELETE_ONLY_ROW)
                .respond();
    }
}