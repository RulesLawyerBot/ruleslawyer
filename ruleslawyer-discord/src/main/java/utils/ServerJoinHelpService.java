package utils;

import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ServerJoinHelpService {

    public static Optional<ServerTextChannel> getChannelToSendMessage(ServerJoinEvent event) {
        Server newServer = event.getServer();

        Optional<ServerTextChannel> systemChannel = newServer.getSystemChannel();

        if (systemChannel.isPresent() && systemChannel.get().canYouWrite())
            return systemChannel;

        Optional<ServerTextChannel> generalChannel = findFirstChannel(newServer.getChannelsByNameIgnoreCase("general").stream());

        if (generalChannel.isPresent())
            return generalChannel;

        return findFirstChannel(newServer.getChannels().stream());
    }

    private static Optional<ServerTextChannel> findFirstChannel(Stream<ServerChannel> stream) {
        return stream.filter(channel -> ServerTextChannel.class.isAssignableFrom(channel.getClass()))
                .map(channel -> (ServerTextChannel)channel)
                .filter(ServerTextChannel::canYouWrite)
                .findFirst();
    }
}
