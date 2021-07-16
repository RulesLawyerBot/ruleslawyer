package search;

import exception.NotYetImplementedException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import search.contract.DiscordSearchResult;
import service.HelpMessageSearchService;
import service.interaction_pagination.ActionRowBuilder;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.javacord.api.interaction.SlashCommandOption.*;
import static org.javacord.api.interaction.SlashCommandOptionType.STRING;
import static service.HelpMessageSearchService.*;

public class SlashCommandSearchService {

    public static final String RULE_SLASH_COMMAND_IDENTIFIER = "rule";
    public static final String CARD_SLASH_COMMAND_IDENTIFIER = "card"; //TODO
    public static final String HELP_SLASH_COMMAND_IDENTIFIER = "help";

    private DiscordApi api;
    private DiscordRuleSearchService discordRuleSearchService;
    private HelpMessageSearchService helpMessageSearchService;

    public SlashCommandSearchService(DiscordApi api, DiscordRuleSearchService discordRuleSearchService) {
        this.api = api;
        this.discordRuleSearchService = discordRuleSearchService;
        this.helpMessageSearchService = new HelpMessageSearchService();
    }

    public void setCommands() {
        List<SlashCommand> commands = api.getGlobalSlashCommands().join();
        commands.forEach(SlashCommand::deleteGlobal);
        SlashCommand.with(
                RULE_SLASH_COMMAND_IDENTIFIER,
                "search RulesLawyer for a rule",
                singletonList(
                        create(
                                STRING,
                                "Query",
                                "Query parameters (surround with \"quotes\" for exact match)",
                                true
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
                                "Query",
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

    public void respondtoSlashCommand(SlashCommandCreateEvent event) {
        String commandName = event.getSlashCommandInteraction().getCommandName();
        if (commandName.equals(RULE_SLASH_COMMAND_IDENTIFIER)) {
            respondtoRuleCommand(event);
        } else if (commandName.equals(HELP_SLASH_COMMAND_IDENTIFIER)) {
            respondToHelpCommand(event);
        } else {
            throw new NotYetImplementedException();
        }
    }

    public void respondtoRuleCommand(SlashCommandCreateEvent event) {
        DiscordSearchResult searchResult =
                discordRuleSearchService.getSearchResultFromPlainQuery(
                        event.getSlashCommandInteraction().getUser().getDiscriminatedName(),
                        event.getSlashCommandInteraction().getFirstOptionStringValue().orElse("")
                );
        if (searchResult.isEmbed()) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(searchResult.getEmbed())
                    .addComponents(ActionRow.of(searchResult.getComponents()))
                    .respond();
        } else {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .setContent(searchResult.getText())
                    .addComponents(ActionRow.of(searchResult.getComponents()))
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
                .addComponents(new ActionRowBuilder().setHasDelete().buildToRow())
                .respond();
    }
}
