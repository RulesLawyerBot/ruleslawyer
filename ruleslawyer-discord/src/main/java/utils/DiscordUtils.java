package utils;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

import static app.DiscordApplicationMain.DEV_SERVER_ID;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

public class DiscordUtils {

    public static String getDiscordKey(String keyId) {
        if (keyId.equals("dev") || keyId.equals("prod") || keyId.equals("test")) {
            try {
                if (keyId.equals("dev")) {
                    return getKeyForIndex(0);
                } else if (keyId.equals("prod")) {
                    return getKeyForIndex(1);
                } else {
                    return getKeyForIndex(2);
                }
            } catch(IOException exception) {
                return keyId;
            }
        }
        return keyId;
    }

    public static List<String> getDiscordKeys(String[] keyId) {
        try {
            if (keyId[0].equals("dev")) {
                return asList(getKeyForIndex(0), getKeyForIndex(3));
            } else if (keyId[0].equals("prod")) {
                return asList(getKeyForIndex(1), getKeyForIndex(4));
            }
            throw new IOException();
        } catch (IOException exception) {
            return asList(keyId);
        }
    }

    private static String getKeyForIndex(Integer index) throws IOException {
        InputStream in = DiscordUtils.class.getResourceAsStream("/keys.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in, UTF_8));
        char[] buffer = new char[1000000];
        br.read(buffer);
        in.close();
        String[] keys = new String(buffer).split("\r\n");
        return keys[index];
    }

    public static Optional<String> getUsernameForMessageCreateEvent(MessageCreateEvent event) {
        return event.getMessageAuthor().asUser().map(User::getDiscriminatedName);
    }

    public static Optional<String> getUsernameForReactionAddEvent(SingleReactionEvent event) {
        return event.getUser().map(User::getDiscriminatedName);
    }

    public static boolean isOwnMessage(MessageCreateEvent event) {
        return event.getMessageAuthor().isYourself();
    }

    public static boolean isLoggingChannel(MessageCreateEvent event) {
        return event.getServer().map(server -> server.getId() == DEV_SERVER_ID).orElse(false) &&
                event.getServerTextChannel().map(channel -> channel.getName().equals("log")).orElse(false);
    }

    public static boolean isOwnMessage(SingleReactionEvent event) {
        return event.getMessageAuthor().map(MessageAuthor::isYourself).orElse(false);
    }

    public static boolean isOwnMessage(MessageEditEvent event) {
        return event.getMessageAuthor().map(MessageAuthor::isYourself).orElse(false);
    }

    public static boolean isOwnReaction(SingleReactionEvent event) {
        return event.getUser().map(User::isYourself).orElse(false);
    }
}
