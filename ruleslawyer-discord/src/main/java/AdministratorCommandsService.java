import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.channel.TextChannel;

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
        if(message.equalsIgnoreCase("ruleslawyer list servers")) {
            listServers(channel);
        }
    }

    private void shutDown() {
        api.disconnect();
        System.exit(0);
    }

    private void listServers(TextChannel channel) {
        Integer numServers = api.getServers().size();
        channel.sendMessage("RulesLawyer is currently running on " + numServers + " servers.");
        String allServers = api.getServers().stream()
                .map(Nameable::getName)
                .collect(joining(", "));
        if (allServers.length() < 2000) {
            channel.sendMessage(allServers);
        } else {
            channel.sendMessage(allServers.substring(0, 2000));
            channel.sendMessage(allServers.substring(2000));
        }
    }
}
