import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public class MessageLoggingService {

    private DiscordApi api;
    private TextChannel loggingChannel;
    public static final Long DEV_SERVER_ID = 590180833118388255L;

    public MessageLoggingService(DiscordApi api) {
        this.api = api;
        loggingChannel = api.getServerById(DEV_SERVER_ID)
                .get()
                .getChannels()
                .stream()
                .filter(channel -> channel.getName().equals("log"))
                .findFirst()
                .get()
                .asTextChannel()
                .get();
    }

    public void logInput(MessageCreateEvent event) {
        Optional<User> user = event.getMessageAuthor().asUser();
        if (!user.isPresent()) {
            return;
        }
        String username = user.get().getName();
        String displayname = username;
        if (event.getServer().isPresent()) {
            displayname = user.get().getDisplayName(event.getServer().get());
        }
        String query = event.getMessage().getContent();
        String output = "**" + username + " (" + displayname + ") asked for: " + query + "**";
        loggingChannel.sendMessage(output);
    }

    public void logOutput(String message) {
        loggingChannel.sendMessage(message);
    }
}
