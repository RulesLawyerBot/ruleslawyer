package service;

import app.DiscordApplicationMain;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Reaction;
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
        /*return !isOwnReaction(event) && isOwnMessage(event)
                && emoji.isPresent() && emoji.get().equals(DELETE_EMOJI);*/

        // workaround because User#isYourself sometimes improperly returns false
        return event.getEmoji().asCustomEmoji().map(e -> e.equals(DELETE_EMOJI)).orElse(false) &&
                isOwnMessage(event) && !isOwnReaction(event) &&
                event.getMessage().map(
                        message ->
                                message.getReactionByEmoji(DELETE_EMOJI).map(reaction -> reaction.getCount() > 1).orElse(false)
                )
                        .orElse(false);
    }
}
