package utils;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class AdministratorCommandsService {

    DiscordApi api;

    public AdministratorCommandsService(DiscordApi api) {
        this.api = api;
    }

    public void processCommand(String message, TextChannel channel) {
        if(message.equalsIgnoreCase("shut down ruleslawyer")) {
            shutDown();
        }
        // DISABLED DUE TO LACK OF INTENT
        /*
        if(message.equalsIgnoreCase("ruleslawyer status")) {
            listServers(channel);
        }
        */
        if(message.equalsIgnoreCase("ruleslawyer verify")) {
            channel.sendMessage("Hi mom!");
            // DISABLED DUE TO LACK OF INTENT
            //listServers(channel);
        }
    }

    private void shutDown() {
        api.disconnect();
        System.exit(0);
    }

    private void listServers(TextChannel channel) {
        Collection<Server> servers = api.getServers();
        Integer totalUsers = (int) servers.stream()
                .map(Server::getMembers)
                .flatMap(Collection::stream)
                .count();
        channel.sendMessage("RulesLawyer is currently running on " + servers.size() + " servers with " + totalUsers + " total users.");
    }
}
