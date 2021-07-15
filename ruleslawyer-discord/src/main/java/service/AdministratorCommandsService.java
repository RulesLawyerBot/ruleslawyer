package service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import search.SlashCommandSearchService;

import java.util.Collection;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class AdministratorCommandsService {

    DiscordApi api;
    SlashCommandSearchService slashCommandSearchService;

    public AdministratorCommandsService(DiscordApi api, SlashCommandSearchService slashCommandSearchService) {
        this.api = api;
        this.slashCommandSearchService = slashCommandSearchService;
    }

    public void processCommand(String message, TextChannel channel) {
        if(message.equalsIgnoreCase("shut down ruleslawyer")) {
            channel.sendMessage("shutting down");
            api.disconnect();
            System.exit(0);
        }
        if(message.equalsIgnoreCase("ruleslawyer status")) {
            countServers(channel);
        }
        if(message.equalsIgnoreCase("ruleslawyer verify")) {
            channel.sendMessage("Hi mom!");
        }
        if(message.equalsIgnoreCase("ruleslawyer reset commands")) {
            try {
                slashCommandSearchService.setCommands();
            } catch (Exception e) {
                channel.sendMessage(ExceptionUtils.getStackTrace(e));
            }
            channel.sendMessage("reset global slash commands");
        }
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
