package app;

import org.javacord.api.entity.channel.TextChannel;
import search.SearchService;
import search.contract.DiscordSearchResult;
import utils.AdministratorCommandsService;
import contract.rules.AbstractRule;
import ingestion.rule.JsonRuleIngestionService;
import utils.MessageLoggingService;
import utils.MessageDeletionService;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import repository.SearchRepository;
import utils.ServerJoinHelpService;

import java.util.List;
import java.util.Optional;

import static chat_platform.HelpMessageService.MAIN_HELP;

public class ApplicationMain {

    private static SearchService searchService;
    private static MessageDeletionService messageDeletionService;
    private static MessageLoggingService messageLoggingService;
    private static AdministratorCommandsService administratorCommandsService;
    public static final Long DEV_SERVER_ID = 590180833118388255L;

    private static final String CURRENT_VERSION = "Version 1.6.0 / JMP";

    public static void main(String[] args) {

        String discordToken = args[0];

        DiscordApi api = new DiscordApiBuilder()
                .setToken(discordToken)
                .login()
                .join();
        try {
            List<AbstractRule> rules = JsonRuleIngestionService.getRules();
            searchService = new SearchService(new SearchRepository<>(rules));
        } catch (Exception ignored) {
            System.exit(-1);
        }

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
}
