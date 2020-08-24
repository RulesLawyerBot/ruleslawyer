package search.contract;

public class DiscordEmbedField {
    private String fieldName;
    private String fieldText;

    public DiscordEmbedField(String fieldName, String fieldText) {
        this.fieldName = fieldName;
        this.fieldText = fieldText;
        if (fieldText.length() == 0)
            this.fieldText = ".";
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldText() {
        return fieldText;
    }

    public Integer getLength() {
        return fieldName.length() + fieldText.length();
    }
}
