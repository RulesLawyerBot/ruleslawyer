package chat_platform.rule_output;

import contract.rules.AbstractRule;
import contract.rules.PrintableRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class OutputFieldSplitService {

    private Integer maxHeaderLength;
    private Integer maxBodyLength;

    public OutputFieldSplitService(Integer maxHeaderLength, Integer maxBodyLength) {
        this.maxHeaderLength = maxHeaderLength;
        this.maxBodyLength = maxBodyLength;
    }

    public List<GenericRuleOutputField> getGenericRuleBlocks(List<AbstractRule> rawRules) {
        return rawRules.stream()
                .map(AbstractRule::getPrintedRules)
                .flatMap(Collection::stream)
                .map(this::getGenericRuleBlocks)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public List<GenericRuleOutputField> getGenericRuleBlocks(PrintableRule rule) {
        String fieldHeader = rule.getHeader();
        String fieldText = rule.getBodyText();

        if (fieldHeader.length() > maxHeaderLength) {
            Integer splitIndex = fieldHeader.substring(0, maxHeaderLength).lastIndexOf(" ");
            fieldText = "..." + fieldHeader.substring(splitIndex+1) + "\n" + fieldText;
            fieldHeader = fieldHeader.substring(0, splitIndex);
        }

        String shortenedFieldHeader =
                fieldHeader.lastIndexOf(". ") == -1 ?
                fieldHeader :
                fieldHeader.substring(0, fieldHeader.lastIndexOf(". ")+1);
        List<String> splitFieldText = splitFieldText(fieldText);

        return concat(
                Stream.of(new GenericRuleOutputField(fieldHeader, splitFieldText.get(0))),
                splitFieldText.subList(1, splitFieldText.size()).stream()
                        .map(text -> new GenericRuleOutputField(shortenedFieldHeader, text))
        ).collect(toList());
    }

    private List<String> splitFieldText(String rawFieldText) {
        ArrayList<String> output = new ArrayList<>();

        while (true) {
            if (rawFieldText.length() < maxBodyLength) {
                output.add(rawFieldText);
                break;
            }
            Integer split = rawFieldText.substring(0, maxBodyLength).lastIndexOf("\n");
            if (split == -1) {
                split = rawFieldText.substring(0, maxBodyLength).lastIndexOf(" ");
            }
            output.add(rawFieldText.substring(0, split));
            rawFieldText = rawFieldText.substring(split+1);
        }

        return output;
    }
}
