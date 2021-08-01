package search.contract;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class DiscordReturnPayload {
    private String content;
    private EmbedBuilder embed;
    private ActionRow components;

    public DiscordReturnPayload(EmbedBuilder embed) {
        this.embed = embed;
    }

    public DiscordReturnPayload setComponents(ActionRow components) {
        this.components = components;
        return this;
    }

    public DiscordReturnPayload setContent(String content) {
        this.content = content;
        return this;
    }

    public EmbedBuilder getEmbed() {
        return embed;
    }

    public ActionRow getComponents() {
        return components;
    }

    public String getContent() {
        return content;
    }

    public MessageBuilder getMessage() {
        return getMessageWithoutButtons()
                .addComponents(components);
    }

    public MessageBuilder getMessageWithoutButtons() {
        if (content == null) {
            return new MessageBuilder()
                    .addEmbed(embed);
        } else {
            return new MessageBuilder()
                    .setContent(content)
                    .addEmbed(embed);
        }
    }
}
