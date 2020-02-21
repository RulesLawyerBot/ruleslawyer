import chat_platform.ChatMessageService;
import contract.rules.AbstractRule;
import ingestion.JsonRuleIngestionService;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import repository.SearchRepository;

import java.util.List;

import static contract.RequestSource.DISCORD;

public class ApplicationMain {

    private static ChatMessageService chatMessageService;

    public static void main(String[] args) {

        String discordToken = args[0]; //dev token

        DiscordApi api = new DiscordApiBuilder()
                .setToken(discordToken)
                .login()
                .join();
        try {
            List<AbstractRule> rules = JsonRuleIngestionService.getRules();
            chatMessageService = new ChatMessageService(DISCORD, new SearchRepository<>(rules, DISCORD));
        } catch (Exception ignored) {

        }

        api.addMessageCreateListener(ApplicationMain::handleEvent);
    }

    public static void handleEvent(MessageCreateEvent event) {
        if (!event.getMessageAuthor().asUser().get().isBot()){
            List<String> output = chatMessageService.processMessage(event.getMessageContent());
            output.forEach(message ->  {
                event.getChannel().sendMessage(message);
                System.out.println(message);
            }
            );
        }
    }
}
