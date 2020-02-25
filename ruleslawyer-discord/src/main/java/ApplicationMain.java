import chat_platform.ChatMessageService;
import contract.rules.AbstractRule;
import ingestion.JsonRuleIngestionService;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import repository.SearchRepository;

import java.util.List;

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
