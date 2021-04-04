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
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DiscordUtils {

    public static String getDiscordKey(String keyId) { //TODO move this why the fuck is it even here
        if(keyId.equals("dev") || keyId.equals("prod")) {
            try {
                InputStream in = DiscordUtils.class.getResourceAsStream("/keys.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(in, UTF_8));
                char[] buffer = new char[1000000];
                br.read(buffer);
                in.close();
                String keys = new String(buffer);
                return keyId.equals("dev") ?
                        keys.substring(0, 59) :
                        keys.substring(59, 119);
            } catch(IOException exception) {
                return keyId;
            }
        }
        return keyId;
    }

    public static Optional<String> getUsernameForMessageCreateEvent(MessageCreateEvent event) {
        return event.getMessageAuthor().asUser().map(User::getDiscriminatedName);
    }

    public static Optional<String> getUsernameForReactionAddEvent(SingleReactionEvent event) {
        return event.getMessageAuthor().map(MessageAuthor::getDiscriminatedName);
    }

    public static boolean isOwnMessage(MessageCreateEvent event) {
        return event.getMessageAuthor().isYourself();
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

    public static boolean isUserMessage(MessageCreateEvent event) {
        return event.getMessageAuthor().asUser().isPresent() && !event.getMessageAuthor().isBotUser();
    }
}
