package service;

import app.DiscordApplicationMain;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

import java.util.Optional;

import static utils.DiscordUtils.isOwnMessage;
import static utils.DiscordUtils.isOwnReaction;

public class MessageDeletionService {
    public static final Long DELETE_EMOTE_ID = 719583524176265256L;
    private CustomEmoji DELETE_EMOJI;

    public MessageDeletionService(DiscordApi api) {
        DELETE_EMOJI = api.getServerById(DiscordApplicationMain.DEV_SERVER_ID).get()
                .getCustomEmojiById(DELETE_EMOTE_ID).get();
    }

    public boolean shouldDeleteMessage(ReactionAddEvent event) {
        Optional<CustomEmoji> emoji = event.getEmoji().asCustomEmoji();
        return !isOwnReaction(event) && isOwnMessage(event)
                && emoji.isPresent() && emoji.get().equals(DELETE_EMOJI);
    }
}
