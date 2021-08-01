package search.contract;

import exception.NotYetImplementedException;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

//blame javacord for the shitty naming of this class
public class EmbedBuilderBuilder {

    private static final Color EMBED_COLOR = new Color(179, 219, 255);

    private String author;
    private String title;
    private List<DiscordEmbedField> fields;
    private String footer;
    private String imageUrl;
    private boolean imageIsThumbnail;
    private boolean hasInlineFields;

    public EmbedBuilderBuilder() {
        fields = new ArrayList<>();
        this.author = "";
        this.title = "";
        this.footer = "";
        this.imageUrl = "";
        this.imageIsThumbnail = true;
        this.hasInlineFields = false;
    }

    public EmbedBuilderBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public EmbedBuilderBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public EmbedBuilderBuilder setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public EmbedBuilderBuilder setThumbnail(String url) {
        this.imageUrl = url;
        this.imageIsThumbnail = true;
        return this;
    }

    public EmbedBuilderBuilder setImage(String url) {
        this.imageUrl = url;
        this.imageIsThumbnail = false;
        return this;
    }

    public EmbedBuilderBuilder setHasInlineFields(boolean hasInlineFields) {
        this.hasInlineFields = hasInlineFields;
        return this;
    }

    public EmbedBuilderBuilder addFields(DiscordEmbedField ... fields) {
        this.fields.addAll(asList(fields));
        return this;
    }

    public EmbedBuilderBuilder addFields(List<DiscordEmbedField> fields) {
        this.fields.addAll(fields);
        return this;
    }

    public EmbedBuilderBuilder removeLastField() {
        if (fields.size() == 0)
            throw new NotYetImplementedException();
        fields.remove(fields.size()-1);
        return this;
    }

    public Integer getLength() {
        return title.length() + author.length() + footer.length()
                + fields.stream()
                .mapToInt(field -> field.getFieldName().length() + field.getFieldText().length())
                .sum();
    }

    public EmbedBuilder build() {
        EmbedBuilder output = new EmbedBuilder()
                .setTitle(title)
                .setAuthor(author, "https://www.ruleslawyer.app", "https://www.ruleslawyer.app/favicon.png")
                .setFooter(footer)
                .setColor(EMBED_COLOR);
        if (hasInlineFields) {
            fields.forEach(field -> output.addInlineField(field.getFieldName(), field.getFieldText()));
        } else {
            fields.forEach(field -> output.addField(field.getFieldName(), field.getFieldText()));
        }
        if (imageUrl.length() > 0) {
            if (imageIsThumbnail) {
                output.setThumbnail(imageUrl);
            } else {
                output.setImage(imageUrl);
            }
        }
        return output;
    }
}
