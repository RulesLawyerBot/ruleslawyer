package service;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import search.SlashCommandSearchService;

import java.util.Collection;

import static java.lang.String.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

public class AdministratorCommandsService {

    DiscordApi api;
    DiscordApi adminApi;
    SlashCommandSearchService slashCommandSearchService;

    public AdministratorCommandsService(DiscordApi api, DiscordApi adminApi, SlashCommandSearchService slashCommandSearchService) {
        this.api = api;
        this.adminApi = api;
        this.slashCommandSearchService = slashCommandSearchService;
    }

    public void processCommand(String message, TextChannel channel) {
        if(message.equalsIgnoreCase("shut down ruleslawyer")) {
            channel.sendMessage("shutting down");
            api.disconnect();
            adminApi.disconnect();
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
                channel.sendMessage(getStackTrace(e));
            }
            channel.sendMessage("reset global slash commands");
        }
    }

    private void countServers(TextChannel channel) {
        Collection<Server> servers = api.getServers();
        Integer totalUsers = (int) servers.stream()
                .map(Server::getMembers)
                .mapToLong(Collection::size)
                .sum();
        Integer uniqueUsers = (int) servers.stream()
                .map(Server::getMembers)
                .flatMap(Collection::stream)
                .distinct()
                .count();
        channel.sendMessage(format("RulesLawyer is current running on %s servers serving %s unique users (%s total).", servers.size(), uniqueUsers, totalUsers));
    }
}
