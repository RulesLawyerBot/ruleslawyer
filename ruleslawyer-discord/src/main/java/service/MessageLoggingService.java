package service;

import app.DiscordApplicationMain;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import service.reaction_pagination.PageDirection;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

public class MessageLoggingService {

    private DiscordApi api;
    private TextChannel loggingChannel;
    private TextChannel joinLoggingChannel;

    public MessageLoggingService(DiscordApi api) {
        this.api = api;

        List<ServerChannel> devServerChannels = api.getServerById(DiscordApplicationMain.DEV_SERVER_ID)
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
        String displayname = event.getServer()
                .map(server -> user.get().getDisplayName(server))
                .orElse(username);
        if (event.getServer().isPresent()) {
            displayname = user.get().getDisplayName(event.getServer().get());
        }
        String query = event.getMessage().getContent();
        String output = format("**%s (%s) asked for: %s**", username, displayname, query);
        loggingChannel.sendMessage(output);
    }

    public void logOutput(EmbedBuilder message) {
        loggingChannel.sendMessage(message);
    }

    public void logOutput(String message) {
        loggingChannel.sendMessage(message);
    }

    public void logEditInput(PageDirection pageDirection, Embed embed) {
        String output = format("**Edit: %s\n%s\n%s**", pageDirection.name(), embed.getTitle().get(), embed.getFooter().get().getText().get());
        loggingChannel.sendMessage(output);
    }

    public void logJoin(Server server) {
        joinLoggingChannel.sendMessage(":tada: RulesLawyer was just added to " + server.getName() + " with " + server.getMembers().size() + " members. " +
                "RulesLawyer is now running on " + api.getServers().size() + " servers.");
    }

    public void logJoinMessageSuccess(Server server) {
        joinLoggingChannel.sendMessage("Introductory message successfully sent for " + server.getName() + ".");
    }
}
