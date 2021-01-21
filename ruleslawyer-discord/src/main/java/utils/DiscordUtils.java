package utils;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

import java.util.Optional;

public class DiscordUtils {

    public static Optional<String> getUsernameForMessageCreateEvent(MessageCreateEvent event) {
        Optional<User> user = event.getMessageAuthor().asUser();
        return user.flatMap(
                value -> event.getServer().isPresent() ?
                Optional.of(value.getDisplayName(event.getServer().get())) :
                Optional.of(value.getName())
        );
    }

    public static Optional<String> getUsernameForReactionAddEvent(ReactionAddEvent event) {
        Optional<User> user = event.getUser();
        return user.flatMap(
                value -> event.getServer().isPresent() ?
                        Optional.of(value.getDisplayName(event.getServer().get())) :
                        Optional.of(value.getName())
        );
    }

    public static boolean isOwnMessage(MessageCreateEvent event) {
        return event.getMessageAuthor().isYourself();
    }

    public static boolean isOwnMessage(ReactionAddEvent event) {
        return event.getMessageAuthor().map(MessageAuthor::isYourself).orElse(false);
    }

    public static boolean isOwnReaction(ReactionAddEvent event) {
        return event.getUser().map(User::isYourself).orElse(false);
    }

    public static boolean isUserMessage(MessageCreateEvent event) {
        return !event.getMessageAuthor().isBotUser();
    }
}
