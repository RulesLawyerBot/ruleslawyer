import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

import java.util.Optional;

public class MessageDeletionService {

    public boolean shouldDeleteMessage(ReactionAddEvent event) {
        Optional<MessageAuthor> messageAuthor = event.getMessageAuthor();
        Optional<String> emoji = event.getEmoji().asUnicodeEmoji();
        if (!messageAuthor.isPresent() || !emoji.isPresent()) {
            return false;
        }
        return messageAuthor.get().isYourself() && emoji.get().equals("✅") && !event.getUser().isBot();
    }
}
