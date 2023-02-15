package app;

import init_utils.ManaEmojiService;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;
import search.DiscordCardSearchService;
import search.DiscordRuleSearchService;
import search.SlashCommandSearchService;
import service.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import search.interaction_pagination.InteractionPaginationService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.javacord.api.entity.intent.Intent.GUILD_PRESENCES;
import static search.interaction_pagination.InteractionPaginationStatics.DELETE_ONLY_ROW;
import static service.HelpMessageSearchService.MAIN_HELP_EMBED;
import static utils.DiscordUtils.*;

public class DiscordApplicationMain {

    private static DiscordRuleSearchService discordRuleSearchService;
    private static DiscordCardSearchService discordCardSearchService;
    private static MessageLoggingService messageLoggingService;
    private static AdministratorCommandsService administratorCommandsService;
    private static SlashCommandSearchService slashCommandSearchService;
    private static InteractionPaginationService interactionPaginationService;
    public static final Long DEV_SERVER_ID = 590180833118388255L;

    private static final String CURRENT_VERSION = "Version 1.14.4 | ONE | \"/help\"";

    public static void main(String[] args) {
        if (!args[0].equals("prod")) {
            FallbackLoggerConfiguration.setDebug(true);
        }
        List<String> discordToken = getDiscordKeys(args);

        System.out.println("API token: \"" + discordToken.get(0) + "\"");
        System.out.println("Admin token: \"" + discordToken.get(1) + "\"");
        DiscordApi api = new DiscordApiBuilder()
                .setToken(discordToken.get(0))
                .setAllIntentsExcept(GUILD_PRESENCES)
                .login()
                .join();

        DiscordApi adminApi = new DiscordApiBuilder()
                .setToken(discordToken.get(1))
                .setAllIntents()
                .login()
                .join();

        System.out.println("Loading rules and cards...");
        ManaEmojiService manaEmojiService = new ManaEmojiService(api);
        discordRuleSearchService = new DiscordRuleSearchService(manaEmojiService);
        discordCardSearchService = new DiscordCardSearchService(manaEmojiService);

        System.out.println("Setting listeners...");

        api.addMessageCreateListener(DiscordApplicationMain::handleMessageCreateEvent);
        api.addServerJoinListener(DiscordApplicationMain::handleServerJoinEvent);
        api.addSlashCommandCreateListener(DiscordApplicationMain::handleSlashCommandCreateEvent);
        api.addMessageComponentCreateListener(DiscordApplicationMain::handleMessageComponentCreateEvent);
        api.addAutocompleteCreateListener(DiscordApplicationMain::handleAutoCompleteCreateEvent);
        adminApi.addMessageCreateListener(DiscordApplicationMain::handleAdminMessageCreateEvent);

        System.out.println("Final setup...");
        try {
            messageLoggingService = new MessageLoggingService(api);
            slashCommandSearchService = new SlashCommandSearchService(api, discordRuleSearchService, discordCardSearchService);
            interactionPaginationService = new InteractionPaginationService(discordRuleSearchService, discordCardSearchService);
            administratorCommandsService = new AdministratorCommandsService(api, adminApi, slashCommandSearchService);

        } catch (NoSuchElementException e) {
            System.out.println("Error in initialization");
            e.printStackTrace();
        }

        System.out.println("Initialization complete");
        api.updateActivity(CURRENT_VERSION);
    }

    private static void handleServerJoinEvent(ServerJoinEvent event) {
        messageLoggingService.logJoin(event.getServer());

        Optional<ServerTextChannel> generalChannel = ServerJoinHelpService.getChannelToSendMessage(event);
        generalChannel.ifPresent(channel -> {
            new MessageBuilder()
                    .setEmbed(MAIN_HELP_EMBED.build())
                    .addComponents(DELETE_ONLY_ROW)
                    .send(channel);
            messageLoggingService.logJoinMessageSuccess(channel.getServer());
        }
        );
    }

    private static void handleMessageCreateEvent(MessageCreateEvent event) {
        if (event.getMessage().getMentionedUsers().stream().anyMatch(User::isYourself)) {
            new MessageBuilder().setEmbed(MAIN_HELP_EMBED.build()).addComponents(DELETE_ONLY_ROW).send(event.getChannel());
        }

        event.getApi().updateActivity(CURRENT_VERSION);
    }

    private static void handleAdminMessageCreateEvent(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().map(User::isBotOwner).orElse(false)) {
            administratorCommandsService.processCommand(event.getMessage().getContent(), event.getChannel());
        }
    }

    private static void handleSlashCommandCreateEvent(SlashCommandCreateEvent event) {
        slashCommandSearchService.respondToSlashCommand(event);

        event.getApi().updateActivity(CURRENT_VERSION);
    }

    private static void handleMessageComponentCreateEvent(MessageComponentCreateEvent event) {
        interactionPaginationService.respondToInteractionCommand(event);
    }

    private static void handleAutoCompleteCreateEvent(AutocompleteCreateEvent event) {
        slashCommandSearchService.respondToAutocomplete(event);
    }
}
