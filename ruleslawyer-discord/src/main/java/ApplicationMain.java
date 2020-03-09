import chat_platform.ChatMessageService;
import contract.rules.AbstractRule;
import ingestion.JsonRuleIngestionService;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
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

    public static void main(String[] args) {

        String discordToken = args[0]; //dev token
        messageDeletionService = new MessageDeletionService();

        DiscordApi api = new DiscordApiBuilder()
                .setToken(discordToken)
                .login()
                .join();
        try {
            List<AbstractRule> rules = JsonRuleIngestionService.getRules();
            chatMessageService = new ChatMessageService(DISCORD, new SearchRepository<>(rules, DISCORD));
        } catch (Exception ignored) {
            System.exit(-1);
        }

        api.addMessageCreateListener(ApplicationMain::handleMessageCreateEvent);

        api.addReactionAddListener(ApplicationMain::handleReactionAddEvent);

        api.addServerJoinListener(ApplicationMain::handleServerJoinEvent);

    }

    private static void handleServerJoinEvent(ServerJoinEvent event) {
        Optional<TextChannel> generalChannel = ServerJoinHelpService.getChannelToSendMessage(event);
        generalChannel.ifPresent(channel -> channel.sendMessage(MAIN_HELP));
    }

    private static void handleReactionAddEvent(ReactionAddEvent event) {
        if (messageDeletionService.shouldDeleteMessage(event)) {
            event.deleteMessage();
        }
    }

    public static void handleMessageCreateEvent(MessageCreateEvent event) {
        if (!event.getMessageAuthor().asUser().get().isBot()){
            List<String> output = chatMessageService.processMessage(event.getMessageContent());
            output.forEach(message ->  {
                        event.getChannel().sendMessage(message);
                        System.out.println(message);
                    }
            );
        }
        if (event.getMessageAuthor().isYourself()) {
            event.getMessage().addReaction("✅");
        }
    }
}
