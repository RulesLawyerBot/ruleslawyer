import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.List;
import java.util.Optional;

public class MessageLoggingService {

    private DiscordApi api;
    private TextChannel loggingChannel;
    private TextChannel joinLoggingChannel;

    public MessageLoggingService(DiscordApi api) {
        this.api = api;

        List<ServerChannel> devServerChannels = api.getServerById(ApplicationMain.DEV_SERVER_ID)
                .get()
                .getChannels();
        loggingChannel = devServerChannels.stream()
                .filter(channel -> channel.getName().equals("log"))
                .findFirst()
                .get()
                .asTextChannel()
                .get();

        joinLoggingChannel = devServerChannels.stream()
                .filter(channel -> channel.getName().equals("join-log"))
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

    public void logJoin(Server server) {
        joinLoggingChannel.sendMessage(":tada: RulesLawyer was just added to " + server.getName() + " with " + server.getMembers().size() + " members. " +
                "RulesLawyer is now running on " + api.getServers().size() + " servers.");
    }
}
