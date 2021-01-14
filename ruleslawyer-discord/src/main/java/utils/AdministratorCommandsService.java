package utils;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;

import java.util.Collection;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class AdministratorCommandsService {

    DiscordApi api;

    public AdministratorCommandsService(DiscordApi api) {
        this.api = api;
    }

    public void processCommand(String message, TextChannel channel) {
        if(message.equalsIgnoreCase("shut down ruleslawyer")) {
            shutDown();
        }
        if(message.equalsIgnoreCase("ruleslawyer status")) {
            countServers(channel);
        }
        if(message.equalsIgnoreCase("ruleslawyer verify")) {
            channel.sendMessage("Hi mom!");
        }
    }

    private void shutDown() {
        api.disconnect();
        System.exit(0);
    }

    private void countServers(TextChannel channel) {
        Collection<Server> servers = api.getServers();
        Integer totalUsers = (int) servers.stream()
                .map(Server::getMembers)
                .flatMap(Collection::stream)
                .count();
        Integer uniqueUsers = (int) servers.stream()
                .map(Server::getMembers)
                .flatMap(Collection::stream)
                .distinct()
                .count();
        channel.sendMessage(format("RulesLawyer is current running on %s servers serving %s unique users (%s total).", servers.size(), uniqueUsers, totalUsers));
    }
}
