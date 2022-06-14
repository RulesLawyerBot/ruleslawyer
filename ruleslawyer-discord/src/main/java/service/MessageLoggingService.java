package service;

import app.DiscordApplicationMain;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import search.interaction_pagination.pagination_enum.PageDirection;

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

    @Deprecated
    public void logOutput(EmbedBuilder message) {
        loggingChannel.sendMessage(message);
    }

    @Deprecated
    public void logOutput(String message) {
        loggingChannel.sendMessage(message);
    }

    public void logJoin(Server server) {
        joinLoggingChannel.sendMessage(":tada: RulesLawyer was just added to " + server.getName() + ".");
        joinLoggingChannel.sendMessage("RulesLawyer is now running on " + api.getServers().size() + " servers.");
    }

    public void logJoinMessageSuccess(Server server) {
        joinLoggingChannel.sendMessage("Introductory message successfully sent for " + server.getName() + ".");
    }
}
