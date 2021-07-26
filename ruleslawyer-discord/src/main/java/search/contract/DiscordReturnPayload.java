package search.contract;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class DiscordReturnPayload {
    private EmbedBuilder embed;
    private ActionRow components;

    public DiscordReturnPayload(EmbedBuilder embed) {
        this.embed = embed;
    }

    public DiscordReturnPayload setComponents(ActionRow components) {
        this.components = components;
        return this;
    }

    public EmbedBuilder getEmbed() {
        return embed;
    }

    public ActionRow getComponents() {
        return components;
    }

    public MessageBuilder getMessage() {
        return getMessageWithoutButtons()
                .addComponents(components);
    }

    public MessageBuilder getMessageWithoutButtons() {
        return new MessageBuilder()
                    .addEmbed(embed);
    }
}
