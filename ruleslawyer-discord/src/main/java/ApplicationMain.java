import chat_platform.ChatMessageService;
import contract.rules.AbstractRule;
import ingestion.rule.JsonRuleIngestionService;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.server.ServerJoinEvent;
import repository.SearchRepository;

import java.util.List;
import java.util.Optional;

import static chat_platform.HelpMessageService.MAIN_HELP;
import static contract.RequestSource.DISCORD;

public class ApplicationMain {

    private static ChatMessageService chatMessageService;
    private static MessageDeletionService messageDeletionService;
    private static MessageLoggingService messageLoggingService;
    private static AdministratorCommandsService administratorCommandsService;

    private static final String CURRENT_VERSION = "Version 1.4.1 / IKO / {{help}}";

    public static void main(String[] args) {

        String discordToken = args[0];
        messageDeletionService = new MessageDeletionService();

        DiscordApi api = new DiscordApiBuilder()
                .setToken(discordToken)
                .login()
                .join();
        try {
            List<AbstractRule> rules = JsonRuleIngestionService.getRules();
            chatMessageService = new ChatMessageService(DISCORD, new SearchRepository<>(rules));
        } catch (Exception ignored) {
            System.exit(-1);
        }

        messageLoggingService = new MessageLoggingService(api);
        administratorCommandsService = new AdministratorCommandsService(api);

        api.updateActivity(CURRENT_VERSION);

        api.addMessageCreateListener(ApplicationMain::handleMessageCreateEvent);

        api.addReactionAddListener(ApplicationMain::handleReactionAddEvent);

        api.addServerJoinListener(ApplicationMain::handleServerJoinEvent);

        System.out.println("up");
    }

    private static void handleServerJoinEvent(ServerJoinEvent event) {
        messageLoggingService.logOutput(":tada: RulesLawyer was just added to " + event.getServer().getName());
        Optional<TextChannel> generalChannel = ServerJoinHelpService.getChannelToSendMessage(event);
        generalChannel.ifPresent(channel -> channel.sendMessage(MAIN_HELP));
    }

    private static void handleReactionAddEvent(ReactionAddEvent event) {
        if (messageDeletionService.shouldDeleteMessage(event)) {
            event.deleteMessage();
        }
    }

    public static void handleMessageCreateEvent(MessageCreateEvent event) {
        Optional<User> messageSender = event.getMessageAuthor().asUser();
        if (messageSender.isPresent() && !messageSender.get().isBot()){
            List<String> output = chatMessageService.processMessage(event.getMessageContent());
            if (output.size() != 0) {
                messageLoggingService.logInput(event);
            }
            output.forEach(message ->  {
                        event.getChannel().sendMessage(message);
                        messageLoggingService.logOutput(message);
                        System.out.println(message);
                    }
            );
        }
        if (messageSender.isPresent() && messageSender.get().isBotOwner()) {
            administratorCommandsService.processCommand(event.getMessage().getContent(), event.getChannel());
        }
        if (event.getMessageAuthor().isYourself()) {
            event.getMessage().addReaction("✅");
        }
        event.getApi().updateActivity(CURRENT_VERSION);
    }
}
