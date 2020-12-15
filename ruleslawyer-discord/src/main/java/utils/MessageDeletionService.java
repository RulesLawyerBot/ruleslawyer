package utils;

import app.ApplicationMain;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

import java.util.Optional;

public class MessageDeletionService {
    public static final Long DELETE_EMOTE_ID = 719583524176265256L;
    private CustomEmoji DELETE_EMOJI;

    public MessageDeletionService(DiscordApi api) {
        DELETE_EMOJI = api.getServerById(ApplicationMain.DEV_SERVER_ID).get()
                .getCustomEmojiById(DELETE_EMOTE_ID).get();
    }

    public boolean shouldDeleteMessage(ReactionAddEvent event) {
        Optional<MessageAuthor> messageAuthor = event.getMessageAuthor();
        Optional<CustomEmoji> emoji = event.getEmoji().asCustomEmoji();
        if (!messageAuthor.isPresent() || !emoji.isPresent()) {
            return false;
        }
        return messageAuthor.get().isYourself() && emoji.get().equals(DELETE_EMOJI) && !(event.getUserId() == messageAuthor.get().getId());
    }
}
