package search.contract;

import org.javacord.api.entity.message.embed.EmbedBuilder;

public class DiscordSearchResult {
    private boolean isEmbed;
    private String text;
    private EmbedBuilder embed;

    public DiscordSearchResult(String text) {
        isEmbed = false;
        this.text = text;
    }

    public DiscordSearchResult(EmbedBuilder embed) {
        isEmbed = true;
        this.embed = embed;
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
}
