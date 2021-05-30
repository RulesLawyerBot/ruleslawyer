package app.slack.service;

import app.slack.contract.SlackAttachment;
import app.slack.contract.SlackBlock;
import app.slack.contract.SlackField;
import app.slack.contract.SlackResponse;
import chat_platform.rule_output.OutputFieldSplitService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import contract.rules.*;
import contract.searchRequests.RuleSearchRequest;
import contract.searchResults.RawRuleSearchResult;
import contract.searchResults.SearchResult;
import org.springframework.stereotype.Service;
import service.RawRuleSearchService;

import java.util.Collection;
import java.util.List;

import static contract.rules.enums.RuleRequestCategory.ANY_RULE_TYPE;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Service
public class SlackSearchService {

    private static final Integer MAX_HEADER_LENGTH = 150;
    private static final Integer MAX_TEXT_LENGTH = 1800;

    private RawRuleSearchService rawRuleSearchService;
    private OutputFieldSplitService outputFieldSplitService;

    public SlackSearchService() {
        rawRuleSearchService = new RawRuleSearchService();
        outputFieldSplitService = new OutputFieldSplitService(MAX_HEADER_LENGTH, MAX_TEXT_LENGTH);
    }

    public SlackResponse searchRules(String query) {
        RuleSearchRequest searchRequest = new RuleSearchRequest(
                asList(query.split(" ")),
                ANY_DOCUMENT,
                0,
                ANY_RULE_TYPE
        );

        RawRuleSearchResult rawResult = rawRuleSearchService.getRawResult(searchRequest);
        List<SlackBlock> attachments = getBlocksForResults(rawResult.getRawResults()).stream()
                .map(block -> new SlackBlock("section", singletonList(block)))
                .collect(toList());
        ObjectMapper mapper = new ObjectMapper();
        attachments.forEach(block -> {
            try {
                System.out.println(block.getFields().get(0).getText().length());
                System.out.println(mapper.writeValueAsString(block));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return new SlackResponse(
                "in_channel",
                singletonList(
                        new SlackBlock(
                                "section",
                                singletonList(
                                        new SlackField(
                                                "mrkdwn",
                                                join(" | ", searchRequest.getKeywords())
                                        )
                                )
                        )
                ),
                singletonList(
                        new SlackAttachment(attachments)
                )
        );
    }

    private List<SlackField> getBlocksForResults(List<SearchResult<AbstractRule>> results) {
        return results.stream()
                .map(result -> result.getEntry().getPrintedRules())
                .flatMap(Collection::stream)
                .map(this::getBlocksForRule)
                .flatMap(Collection::stream)
                .limit(6)
                .collect(toList());
    }

    private List<SlackField> getBlocksForRule(PrintedRule rule) {
        return outputFieldSplitService.getGenericRuleBlocks(rule).stream()
                .map(ruleField -> new SlackField(
                        "mrkdwn", format("*%s*\n%s", rule.getHeader(), rule.getBodyText())
                ))
                .collect(toList());
    }
}
