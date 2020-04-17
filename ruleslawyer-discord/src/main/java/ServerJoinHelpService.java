import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.server.ServerJoinEvent;

import java.util.List;
import java.util.Optional;

public class ServerJoinHelpService {

    public static Optional<TextChannel> getChannelToSendMessage(ServerJoinEvent event) {
        List<ServerChannel> generalChannels = event.getServer().getChannelsByNameIgnoreCase("general");

        return generalChannels.stream()
                .filter(channel -> TextChannel.class.isAssignableFrom(channel.getClass()))
                .map(channel -> (TextChannel)channel)
                .filter(TextChannel::canYouWrite)
                .findFirst();
    }
}
