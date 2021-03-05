package utils;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;

import java.util.Optional;

public class DiscordUtils {

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

    public static boolean isOwnReaction(SingleReactionEvent event) {
        return event.getUser().map(User::isYourself).orElse(false);
    }

    public static boolean isUserMessage(MessageCreateEvent event) {
        return !event.getMessageAuthor().isBotUser();
    }
}
