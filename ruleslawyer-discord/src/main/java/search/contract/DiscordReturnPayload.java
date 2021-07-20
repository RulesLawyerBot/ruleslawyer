package search.contract;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class DiscordReturnPayload {
    private boolean isEmbed;
    private String text;
    private EmbedBuilder embed;
    private ActionRow components;

    public DiscordReturnPayload(String text) {
        isEmbed = false;
        this.text = text;
    }

    public DiscordReturnPayload(EmbedBuilder embed) {
        isEmbed = true;
        this.embed = embed;
    }

    public DiscordReturnPayload setComponents(ActionRow components) {
        this.components = components;
        return this;
    }

    public boolean isEmbed() {
        return isEmbed;
    }

    public String getText() {
        return text;
    }

    public EmbedBuilder getEmbed() {
        return embed;
    }

    public ActionRow getComponents() {
        return components;
    }

    public MessageBuilder getMessage() {
        return getMessageWithoutButtons().addComponents(components);
    }

    public MessageBuilder getMessageWithoutButtons() {
        if (isEmbed) {
            return new MessageBuilder()
                    .addEmbed(embed);
        } else {
            return new MessageBuilder()
                    .setContent(text);
        }
    }
}
