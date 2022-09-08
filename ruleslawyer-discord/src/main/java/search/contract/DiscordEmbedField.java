package search.contract;

import java.util.Objects;

public class DiscordEmbedField {
    private String fieldName;
    private String fieldText;
    private Integer relevancy;

    public DiscordEmbedField(String fieldName, String fieldText) {
        this.fieldName = fieldName;
        this.fieldText = fieldText;
        if (fieldText.length() == 0)
            this.fieldText = "\u200b";
        this.relevancy = 0;
    }

    public DiscordEmbedField(String fieldName, String fieldText, Integer relevancy) {
        this.fieldName = fieldName;
        this.fieldText = fieldText;
        if (fieldText.length() == 0)
            this.fieldText = ".";
        this.relevancy = relevancy;
    }

    public void setRelevancy(Integer relevancy) {
        this.relevancy = relevancy;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldText() {
        return fieldText;
    }

    public Integer getRelevancy() {
        return relevancy;
    }

    public Integer getLength() {
        return fieldName.length() + fieldText.length();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscordEmbedField)) return false;
        DiscordEmbedField that = (DiscordEmbedField) o;
        return fieldName.equals(that.fieldName) &&
                fieldText.equals(that.fieldText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, fieldText);
    }
}
