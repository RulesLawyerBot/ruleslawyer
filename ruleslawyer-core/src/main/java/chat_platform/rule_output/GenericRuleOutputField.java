package chat_platform.rule_output;

public class GenericRuleOutputField {

    private String header;
    private String body;

    public GenericRuleOutputField(String header, String body) {
        this.header = header;
        this.body = body;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }
}
