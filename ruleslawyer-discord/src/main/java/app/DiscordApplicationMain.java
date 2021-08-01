package app;

import init_utils.ManaEmojiService;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;
import search.DiscordCardSearchService;
import search.DiscordRuleSearchService;
import search.SlashCommandSearchService;
import search.contract.DiscordReturnPayload;
import service.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import search.interaction_pagination.InteractionPaginationService;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.javacord.api.entity.intent.Intent.GUILD_PRESENCES;
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

    private static final String CURRENT_VERSION = "Version 1.12.0 | AFR | \"/help\"";

    public static void main(String[] args) {
        if (!args[0].equals("prod")) {
            FallbackLoggerConfiguration.setDebug(true);
        }
        String discordToken = getDiscordKey(args[0]);

        System.out.println("Logging in with " + discordToken);
        DiscordApi api = new DiscordApiBuilder()
                .setToken(discordToken)
                .setAllIntentsExcept(GUILD_PRESENCES)
                .login()
                .join();

        System.out.println("Loading rules amd cards...");
        ManaEmojiService manaEmojiService = new ManaEmojiService(api);
        discordRuleSearchService = new DiscordRuleSearchService(manaEmojiService);
        discordCardSearchService = new DiscordCardSearchService(manaEmojiService);

        System.out.println("Setting listeners...");

        api.addMessageCreateListener(DiscordApplicationMain::handleMessageCreateEvent);
        api.addServerJoinListener(DiscordApplicationMain::handleServerJoinEvent);
        api.addSlashCommandCreateListener(DiscordApplicationMain::handleSlashCommandCreateEvent);
        api.addMessageComponentCreateListener(DiscordApplicationMain::handleMessageComponentCreateEvent);

        System.out.println("Final setup...");
        try {
            messageLoggingService = new MessageLoggingService(api);
            slashCommandSearchService = new SlashCommandSearchService(api, discordRuleSearchService, discordCardSearchService);
            interactionPaginationService = new InteractionPaginationService(discordRuleSearchService, discordCardSearchService);
            administratorCommandsService = new AdministratorCommandsService(api, slashCommandSearchService);

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
            channel.sendMessage(MAIN_HELP_EMBED);
            messageLoggingService.logJoinMessageSuccess(channel.getServer());
        }
        );
    }

    private static void handleMessageCreateEvent(MessageCreateEvent event) {
        if (isLoggingChannel(event) || !event.getMessageAuthor().isUser()) {
            return;
        }
        DiscordReturnPayload result = discordRuleSearchService.getSearchResult(
                getUsernameForMessageCreateEvent(event).get(),
                event.getMessageContent()
        );
        if (result != null) {
            messageLoggingService.logInput(event);
            result.getMessage().replyTo(event.getMessage()).send(event.getChannel());
            messageLoggingService.logOutput(result.getMessageWithoutButtons());
        }
        if (event.getMessageAuthor().asUser().map(User::isBotOwner).orElse(false)) {
            administratorCommandsService.processCommand(event.getMessage().getContent(), event.getChannel());
        }

        event.getApi().updateActivity(CURRENT_VERSION);
    }

    private static void handleSlashCommandCreateEvent(SlashCommandCreateEvent event) {
        slashCommandSearchService.respondToSlashCommand(event);
    }

    private static void handleMessageComponentCreateEvent(MessageComponentCreateEvent event) {
        interactionPaginationService.respondToInteractionCommand(event);
    }
}
