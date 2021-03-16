package app;

import init_utils.ManaEmojiService;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import search.RuleSearchService;
import search.contract.DiscordSearchResult;
import service.*;
import contract.rules.AbstractRule;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import repository.SearchRepository;
import service.reaction_pagination.ReactionPaginationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

import static chat_platform.HelpMessageService.MAIN_HELP;
import static ingestion.rule.JsonRuleIngestionService.getDigitalEventRules;
import static ingestion.rule.JsonRuleIngestionService.getRules;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.javacord.api.entity.intent.Intent.GUILD_PRESENCES;
import static utils.DiscordUtils.*;

public class ApplicationMain {

    private static RuleSearchService ruleSearchService;
    private static MessageDeletionService messageDeletionService;
    private static ManaEmojiService manaEmojiService;
    private static MessageLoggingService messageLoggingService;
    private static AdministratorCommandsService administratorCommandsService;
    private static ReactionPaginationService reactionPaginationService;
    public static final Long DEV_SERVER_ID = 590180833118388255L;

    private static final String CURRENT_VERSION = "Version 1.8.2 / KHM / {{help|dev}}";

    public static void main(String[] args) {
        String discordToken = getDiscordKey(args[0]);
        System.out.println("Logging in with " + discordToken);

        DiscordApi api = new DiscordApiBuilder()
                .setToken(discordToken)
                .setAllIntentsExcept(GUILD_PRESENCES)
                .login()
                .join();

        System.out.println("Loading rules...");
        manaEmojiService = new ManaEmojiService(api);

        try {
            List<AbstractRule> rules = getRules().stream()
                    .map(manaEmojiService::replaceManaSymbols)
                    .collect(toList());
            List<AbstractRule> digitalRules = getDigitalEventRules().stream()
                    .map(manaEmojiService::replaceManaSymbols)
                    .collect(toList());
            ruleSearchService = new RuleSearchService(new SearchRepository<>(rules), new SearchRepository<>(digitalRules));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Setting listeners...");
        api.updateActivity(CURRENT_VERSION);
        api.addMessageCreateListener(ApplicationMain::handleMessageCreateEvent);
        api.addReactionAddListener(ApplicationMain::handleReactionEvent);
        api.addReactionRemoveListener(ApplicationMain::handleReactionEvent);
        api.addServerJoinListener(ApplicationMain::handleServerJoinEvent);
        api.addMessageEditListener(ApplicationMain::handleMessageEditEvent);

        System.out.println("Final setup...");
        messageDeletionService = new MessageDeletionService(api);
        messageLoggingService = new MessageLoggingService(api);
        administratorCommandsService = new AdministratorCommandsService(api);
        reactionPaginationService = new ReactionPaginationService(ruleSearchService, messageLoggingService);

        System.out.println("Initialization complete");
    }

    private static void handleServerJoinEvent(ServerJoinEvent event) {
        messageLoggingService.logJoin(event.getServer());

        Optional<ServerTextChannel> generalChannel = ServerJoinHelpService.getChannelToSendMessage(event);
        generalChannel.ifPresent(channel -> channel.sendMessage(MAIN_HELP));
        generalChannel.ifPresent(channel -> messageLoggingService.logJoinMessageSuccess(channel.getServer()));
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


    private static void handleMessageCreateEvent(MessageCreateEvent event) {
        Optional<User> messageSender = event.getMessageAuthor().asUser();
        if (isUserMessage(event)){
            DiscordSearchResult result = ruleSearchService.getSearchResult(
                    getUsernameForMessageCreateEvent(event).get(),
                    event.getMessageContent()
            );
            if (result != null) {
                messageLoggingService.logInput(event);
                TextChannel channel = event.getChannel();
                if (result.isEmbed()) {
                    channel.sendMessage(result.getEmbed());
                    messageLoggingService.logOutput(result.getEmbed());
                } else {
                    channel.sendMessage(result.getText());
                    messageLoggingService.logOutput(result.getText());
                }
            }
            if (messageSender.isPresent() && messageSender.get().isBotOwner()) {
                administratorCommandsService.processCommand(event.getMessage().getContent(), event.getChannel());
            }
        }

        if (isOwnMessage(event)) {
            reactionPaginationService.placePaginationReactions(event);
        }

        event.getApi().updateActivity(CURRENT_VERSION);
    }

    private static void handleMessageEditEvent(MessageEditEvent event) {
        if (!isOwnMessage(event)) {
            return;
        }
        reactionPaginationService.replaceSourceChangeReactions(event);
    }
}
