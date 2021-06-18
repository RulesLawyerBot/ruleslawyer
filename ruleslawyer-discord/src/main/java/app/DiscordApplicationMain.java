package app;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import search.DiscordRuleSearchService;
import search.contract.DiscordSearchResult;
import service.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import service.reaction_pagination.ReactionPaginationService;

import java.util.NoSuchElementException;
import java.util.Optional;

import static chat_platform.HelpMessageService.MAIN_HELP;
import static org.javacord.api.entity.intent.Intent.GUILD_PRESENCES;
import static utils.DiscordUtils.*;

public class DiscordApplicationMain {

    private static DiscordRuleSearchService discordRuleSearchService;
    private static MessageDeletionService messageDeletionService;
    private static MessageLoggingService messageLoggingService;
    private static AdministratorCommandsService administratorCommandsService;
    private static ReactionPaginationService reactionPaginationService;
    public static final Long DEV_SERVER_ID = 590180833118388255L;

    private static final String CURRENT_VERSION = "Version 1.10.3 / STX / {{help|dev}}";

    public static void main(String[] args) {
        String discordToken = getDiscordKey(args[0]);
        System.out.println("Logging in with " + discordToken);

        DiscordApi api = new DiscordApiBuilder()
                .setToken(discordToken)
                .setAllIntentsExcept(GUILD_PRESENCES)
                .login()
                .join();

        System.out.println("Loading rules...");
        discordRuleSearchService = new DiscordRuleSearchService(api);

        System.out.println("Setting listeners...");

        api.addMessageCreateListener(DiscordApplicationMain::handleMessageCreateEvent);
        api.addReactionAddListener(DiscordApplicationMain::handleReactionEvent);
        api.addReactionRemoveListener(DiscordApplicationMain::handleReactionEvent);
        api.addServerJoinListener(DiscordApplicationMain::handleServerJoinEvent);
        api.addMessageEditListener(DiscordApplicationMain::handleMessageEditEvent);

        System.out.println("Final setup...");
        try {
            messageDeletionService = new MessageDeletionService(api);
            messageLoggingService = new MessageLoggingService(api);
            administratorCommandsService = new AdministratorCommandsService(api);
            reactionPaginationService = new ReactionPaginationService(discordRuleSearchService, messageLoggingService);
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
            channel.sendMessage(MAIN_HELP);
            messageLoggingService.logJoinMessageSuccess(channel.getServer());
        }
        );
    }

    private static void handleMessageCreateEvent(MessageCreateEvent event) {
        if (isLoggingChannel(event)) {
            return;
        }
        if (isOwnMessage(event)) {
            reactionPaginationService.placePaginationReactions(event);
        } else {
            DiscordSearchResult result = discordRuleSearchService.getSearchResult(
                    getUsernameForMessageCreateEvent(event).get(),
                    event.getMessageContent()
            );
            if (result != null) {
                messageLoggingService.logInput(event);
                if (result.isEmbed()) {
                    event.getMessage().reply(result.getEmbed());
                    messageLoggingService.logOutput(result.getEmbed());
                } else {
                    event.getMessage().reply(result.getText());
                    messageLoggingService.logOutput(result.getText());
                }
            }
            if (event.getMessageAuthor().asUser().map(User::isBotOwner).orElse(false)) {
                administratorCommandsService.processCommand(event.getMessage().getContent(), event.getChannel());
            }
        }

        event.getApi().updateActivity(CURRENT_VERSION);
    }

    private static void handleReactionEvent(SingleReactionEvent event) {
        if (
                event instanceof ReactionAddEvent &&
                        messageDeletionService.shouldDeleteMessage((ReactionAddEvent)event)
        ) {
            event.deleteMessage();
        }
        if (isOwnMessage(event) && !isOwnReaction(event)) {
            reactionPaginationService.handleReactionPaginationEvent(event);
        }
    }

    private static void handleMessageEditEvent(MessageEditEvent event) {
        if (!isOwnMessage(event)) {
            return;
        }
        reactionPaginationService.replaceSourceChangeReactions(event);
    }
}
