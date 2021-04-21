package contract.rules;

public class PrintedRule {
    private String header;
    private String bodyText;

    public PrintedRule(String header, String bodyText) {
        this.header = header;
        this.bodyText = bodyText;
    }

    public String getHeader() {
        return header;
    }

    public String getBodyText() {
        return bodyText;
    }
}
