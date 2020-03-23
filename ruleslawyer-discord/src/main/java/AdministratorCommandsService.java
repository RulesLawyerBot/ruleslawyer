import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;

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
    }
}
