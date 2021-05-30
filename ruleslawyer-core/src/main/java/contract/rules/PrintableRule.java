package contract.rules;

public class PrintableRule {
    private String header;
    private String bodyText;

    public PrintableRule(String header, String bodyText) {
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
