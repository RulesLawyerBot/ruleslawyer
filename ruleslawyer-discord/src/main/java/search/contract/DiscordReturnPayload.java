package search.contract;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DiscordReturnPayload {
    private String content;
    private EmbedBuilderBuilder embed;
    private ActionRow[] components;

    public DiscordReturnPayload(EmbedBuilderBuilder embed) {
        this.embed = embed;
        this.components = new ActionRow[0];
    }

    public DiscordReturnPayload setComponents(ActionRow ... components) {
        this.components = components;
        return this;
    }

    public DiscordReturnPayload setContent(String content) {
        this.content = content;
        return this;
    }

    public EmbedBuilderBuilder getEmbed() {
        return embed;
    }

    public ActionRow[] getComponents() {
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
                    .addEmbed(embed.build());
        } else {
            return new MessageBuilder()
                    .setContent(content)
                    .addEmbed(embed.build());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscordReturnPayload)) return false;
        DiscordReturnPayload that = (DiscordReturnPayload) o;
        return Objects.equals(content, that.content) &&
                Objects.equals(embed, that.embed) &&
                Arrays.equals(components, that.components);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(content, embed);
        result = 31 * result + Arrays.hashCode(components);
        return result;
    }
}
