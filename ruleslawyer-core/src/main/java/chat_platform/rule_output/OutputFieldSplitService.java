package chat_platform.rule_output;

import contract.rules.AbstractRule;
import contract.rules.PrintedRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

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

    public List<GenericRuleOutputField> getGenericRuleBlocks(PrintedRule rule) {
        String fieldHeader = rule.getHeader();
        String fieldText = rule.getBodyText();

        if (fieldHeader.length() > maxHeaderLength) {
            Integer splitIndex = fieldHeader.substring(0, maxHeaderLength).lastIndexOf(" ");
            fieldText = "..." + fieldHeader.substring(splitIndex+1) + "\n" + fieldText;
            fieldHeader = fieldHeader.substring(0, splitIndex);
        }

        String finalFieldHeader = fieldHeader;//are you kidding me java compiler
        if (finalFieldHeader.length() < 128)
            return splitFieldText(fieldText).stream()
                .map(text -> new GenericRuleOutputField(finalFieldHeader, text))
                .collect(toList());
        else {
            List<String> splitFieldText = splitFieldText(fieldText);
            if (splitFieldText.size() < 2) {
                return singletonList(new GenericRuleOutputField(finalFieldHeader, splitFieldText.get(0)));
            } else {
                List<GenericRuleOutputField> output = splitFieldText.stream()
                        .map(field ->
                                new GenericRuleOutputField(finalFieldHeader.substring(0, 9), field)
                        )
                        .collect(toList());
                output.add(0,
                        new GenericRuleOutputField(
                            finalFieldHeader,
                            splitFieldText.get(0)
                        )
                );
                output.remove(1);
                return output;
            }
        }
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
