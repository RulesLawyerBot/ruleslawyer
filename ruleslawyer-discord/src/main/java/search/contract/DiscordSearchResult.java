package search.contract;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.List;

import static java.util.Arrays.asList;

public class DiscordSearchResult {
    private boolean isEmbed;
    private String text;
    private EmbedBuilder embed;
    private List<LowLevelComponent> components;

    public DiscordSearchResult(String text) {
        isEmbed = false;
        this.text = text;
    }

    public DiscordSearchResult(EmbedBuilder embed) {
        isEmbed = true;
        this.embed = embed;
    }

    public DiscordSearchResult setComponents(LowLevelComponent ... components) {
        this.components = asList(components);
        return this;
    }

    public DiscordSearchResult setComponents(List<LowLevelComponent> components) {
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

    public List<LowLevelComponent> getComponents() {
        return components;
    }

    public MessageBuilder getMessage() {
        return getMessageWithoutButtons().addComponents(ActionRow.of(components));
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
