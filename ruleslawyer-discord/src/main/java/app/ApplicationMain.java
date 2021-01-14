package app;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import search.SearchService;
import search.contract.DiscordSearchResult;
import utils.*;
import contract.rules.AbstractRule;
import ingestion.rule.JsonRuleIngestionService;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import repository.SearchRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

import static chat_platform.HelpMessageService.MAIN_HELP;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class ApplicationMain {

    private static JsonRuleIngestionService jsonRuleIngestionService;
    private static SearchService searchService;
    private static MessageDeletionService messageDeletionService;
    private static ManaEmojiService manaEmojiService;
    private static MessageLoggingService messageLoggingService;
    private static AdministratorCommandsService administratorCommandsService;
    public static final Long DEV_SERVER_ID = 590180833118388255L;

    private static final String CURRENT_VERSION = "Version 1.7.2 / CMR / {{help|dev}}";

    public static void main(String[] args) {

        System.out.println("Logging in...");
        String discordToken = getKey(args[0]);

        DiscordApi api = new DiscordApiBuilder()
                .setToken(discordToken)
                .setAllIntentsExcept(Intent.GUILD_PRESENCES)
                .login()
                .join();

        jsonRuleIngestionService = new JsonRuleIngestionService();
        manaEmojiService = new ManaEmojiService(api);

        System.out.println("Loading rules...");
        try {
            List<AbstractRule> rules = jsonRuleIngestionService.getRules();
            List<AbstractRule> emojiReplacedRules = rules.stream()
                    .map(manaEmojiService::replaceManaSymbols)
                    .collect(toList());
            searchService = new SearchService(new SearchRepository<>(emojiReplacedRules));
        } catch (Exception ignored) {
            System.exit(-1);
        }

        System.out.println("Setting listeners...");
        messageDeletionService = new MessageDeletionService(api);
        messageLoggingService = new MessageLoggingService(api);
        administratorCommandsService = new AdministratorCommandsService(api);

        api.updateActivity(CURRENT_VERSION);
        api.addMessageCreateListener(ApplicationMain::handleMessageCreateEvent);
        api.addReactionAddListener(ApplicationMain::handleReactionAddEvent);
        api.addServerJoinListener(ApplicationMain::handleServerJoinEvent);
        System.out.println("Initialization complete");
    }

    private static void handleServerJoinEvent(ServerJoinEvent event) {
        messageLoggingService.logJoin(event.getServer());

        Optional<ServerTextChannel> generalChannel = ServerJoinHelpService.getChannelToSendMessage(event);
        generalChannel.ifPresent(channel -> channel.sendMessage(MAIN_HELP));
        generalChannel.ifPresent(channel -> messageLoggingService.logJoinMessageSuccess(channel.getServer()));
    }

    private static void handleReactionAddEvent(ReactionAddEvent event) {
        if (messageDeletionService.shouldDeleteMessage(event)) {
            event.deleteMessage();
        }
    }


    private static void handleMessageCreateEvent(MessageCreateEvent event) {
        Optional<User> messageSender = event.getMessageAuthor().asUser();
        if (messageSender.isPresent() && !messageSender.get().isBot()){
            String author = event.getServer().isPresent() ?
                    messageSender.get().getDisplayName(event.getServer().get()) :
                    messageSender.get().getName();
            DiscordSearchResult result = searchService.getSearchResult(author, event.getMessageContent());
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
        }
        if (messageSender.isPresent() && messageSender.get().isBotOwner()) {
            administratorCommandsService.processCommand(event.getMessage().getContent(), event.getChannel());
        }

        if (event.getMessageAuthor().isYourself()) {
            event.getMessage().addReaction("javacord:" + MessageDeletionService.DELETE_EMOTE_ID);
        }

        event.getApi().updateActivity(CURRENT_VERSION);
    }

    public static String getKey(String keyId) {
        if(keyId.equals("dev") || keyId.equals("prod")) {
            try {
                InputStream in = ApplicationMain.class.getResourceAsStream("/keys.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(in, UTF_8));
                char[] buffer = new char[1000000];
                br.read(buffer);
                in.close();
                String keys = new String(buffer);
                return keyId.equals("dev") ?
                        keys.substring(0, 59) :
                        keys.substring(59, 119);
            } catch(IOException exception) {
                return keyId;
            }
        }
        return keyId;
    }
}
