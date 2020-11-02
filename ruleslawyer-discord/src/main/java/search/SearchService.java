package search;

import chat_platform.ChatMessageService;
import chat_platform.HelpMessageService;
import contract.searchResults.SearchResult;
import contract.rules.AbstractRule;
import contract.rules.Rule;
import contract.rules.RuleHeader;
import contract.rules.RuleSubheader;
import exception.NotYetImplementedException;
import repository.SearchRepository;
import search.contract.DiscordEmbedField;
import search.contract.DiscordSearchRequest;
import search.contract.DiscordSearchResult;
import search.contract.EmbedBuilderBuilder;
import search.contract.builder.DiscordSearchRequestBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static contract.RuleSource.valueOf;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static search.contract.builder.DiscordSearchRequestBuilder.aDiscordSearchRequest;

public class SearchService {

    private ChatMessageService chatMessageService;
    private HelpMessageService helpMessageService;

    public static final Integer MAX_PAYLOAD_SIZE = 3000;
    public static final Integer MAX_FIELD_NAME_SIZE = 256;
    public static final Integer MAX_FIELD_VALUE_SIZE = 1024;
    private static final String NO_RESULTS_MESSAGE = "No results found :( If you believe this to be an error, please let me know at {{help|about}}. Otherwise, make sure you spelled everything correctly.";

    public SearchService(SearchRepository<AbstractRule> searchRepository) {
        this.chatMessageService = new ChatMessageService(searchRepository);
        this.helpMessageService = new HelpMessageService();
    }

    public DiscordSearchResult getSearchResult(String author, String text) {
        String query = chatMessageService.getQuery(text);
        if (query.equals(""))
            return null;

        if (query.startsWith("help")) {
            return query.equals("help") ?
                    new DiscordSearchResult(helpMessageService.getHelpFile()) :
                    new DiscordSearchResult(helpMessageService.getHelpFile(query.substring(5)));
        }

        DiscordSearchRequest discordSearchRequest = getSearchRequest(author, query);
        List<SearchResult<AbstractRule>> rawResults = chatMessageService.processMessage(discordSearchRequest);

        if (rawResults.size() == 0) {
            return new DiscordSearchResult(NO_RESULTS_MESSAGE);
        }

        EmbedBuilderBuilder result = new EmbedBuilderBuilder()
                .setAuthor("RulesLawyer").setTitle(discordSearchRequest.toString());

        List<DiscordEmbedField> embedFields = rawResults.stream()
                .map(this::getFieldsForRawResult)
                .flatMap(Collection::stream)
                .collect(toList());
        List<List<DiscordEmbedField>> pages = getResultPages(embedFields);

        result.addFields(pages.get(discordSearchRequest.getPageNumber()));

        String footer = "Requested by: " + author + " | "
                + pages.size() + (pages.size() == 1 ? " page" : " pages") + " total | ";
        if (discordSearchRequest.getPageNumber() < pages.size()-1) {
            footer += chatMessageService.getQueryForNextPage(discordSearchRequest) + " for next page";
        } else {
            footer += "No more pages";
        }

        result.setFooter(footer);

        return new DiscordSearchResult(result.build());
    }

    private DiscordSearchRequest getSearchRequest(String author, String query) {
        DiscordSearchRequestBuilder ruleSearchRequest = aDiscordSearchRequest().setRequester(author);

        List<String> commands = asList(query.split("\\|"));

        commands.forEach(
                command -> {
                    if(command.startsWith("p")) {
                        try {
                            ruleSearchRequest.setPageNumber(parseInt(command.substring(1)));
                        } catch (NumberFormatException ignored) {
                            ruleSearchRequest.appendKeywords(getKeywordsFromSubquery(command));
                        }
                    }
                    else if (command.equalsIgnoreCase("digital")) {
                        ruleSearchRequest.setDigitalRuleRequest(true);
                    }
                    else {
                        try {
                            ruleSearchRequest.setRuleSource(valueOf(command.toUpperCase()));
                        } catch (IllegalArgumentException ignored) {
                            ruleSearchRequest.appendKeywords(getKeywordsFromSubquery(command));
                        }
                    }
                }
        );

        return ruleSearchRequest.build();
    }

    private List<String> getKeywordsFromSubquery(String subquery) {
        if (subquery.startsWith("\"") && subquery.endsWith("\"")) {
            return singletonList(subquery.substring(1, subquery.length()-1).toLowerCase());
        }
        else {
            return stream(subquery.split(" "))
                    .map(String::toLowerCase)
                    .collect(toList());
        }
    }

    private List<DiscordEmbedField> getFieldsForRawResult(SearchResult<AbstractRule> result) {
        AbstractRule rule = result.getEntry();
        if (rule.getClass() == Rule.class) {
            return getFieldForBaseRule((Rule) rule);
        }
        if (rule.getClass() == RuleSubheader.class) {
            return getFieldForRuleSubheader((RuleSubheader) rule);
        }
        if (rule.getClass() == RuleHeader.class) {
            return getFieldForRuleHeader((RuleHeader) rule);
        }
        throw new NotYetImplementedException();
    }

    private List<DiscordEmbedField> getFieldForBaseRule(Rule rule) {
        return makeEmbedFieldsForRawText(
                rule.getRuleSource() + " " + rule.getHeader().getText() + " " + rule.getSubHeader().getText(),
                rule.getText()
        );
    }

    private List<DiscordEmbedField> getFieldForRuleSubheader(RuleSubheader rule) {
        if (rule.getSubRules().size() == 0) {
            return makeEmbedFieldsForRawText(rule.getRuleSource() + " " + rule.getHeader().getText(), rule.getText());
        } else {
            return makeEmbedFieldsForRawText(
                    rule.getRuleSource() + " " + rule.getHeader().getText() + " " + rule.getText(),
                    rule.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
            );
        }
    }

    private List<DiscordEmbedField> getFieldForRuleHeader(RuleHeader rule) {
        if (rule.getSubRules().stream().allMatch(subRule -> subRule.getSubRules().size() == 0)) {
            return makeEmbedFieldsForRawText(
                    rule.getRuleSource() + " " + rule.getText(),
                    rule.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
                    );
        }
        return rule.getSubRules().stream().map(
                subrule -> makeEmbedFieldsForRawText(
                        rule.getRuleSource() + " " + rule.getText() + " " + subrule.getText(),
                        subrule.getSubRules().stream().map(AbstractRule::getText).collect(joining("\n"))
                )
        )
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<DiscordEmbedField> makeEmbedFieldsForRawText(String rawFieldName, String rawFieldText) {
        if (rawFieldName.length() < MAX_FIELD_NAME_SIZE && rawFieldText.length() < MAX_FIELD_VALUE_SIZE) {
            return singletonList(new DiscordEmbedField(rawFieldName, rawFieldText));
        }
        if (rawFieldName.length() > MAX_FIELD_NAME_SIZE) {
            Integer split = rawFieldName.substring(0, MAX_FIELD_NAME_SIZE).lastIndexOf(" ");
            String fieldName = rawFieldName.substring(0, split);
            String fieldNameRemainder = "..." + rawFieldName.substring(split+1);
            if (fieldNameRemainder.length () + rawFieldText.length() < MAX_FIELD_VALUE_SIZE-1) {
                return singletonList(new DiscordEmbedField(fieldName, fieldNameRemainder + "\n" + rawFieldText));
            } else {
                return splitFieldText(fieldNameRemainder + "\n" + rawFieldText, MAX_FIELD_VALUE_SIZE).stream()
                        .map(fieldText -> new DiscordEmbedField(fieldName, fieldText))
                        .collect(toList());
            }
        }
        return splitFieldText(rawFieldText, MAX_FIELD_VALUE_SIZE).stream()
                .map(fieldText -> new DiscordEmbedField(rawFieldName, fieldText))
                .collect(toList());
    }

    private List<String> splitFieldText(String rawFieldText, Integer maxSize) {
        ArrayList<String> output = new ArrayList<>();

        while (true) {
            if (rawFieldText.length() < maxSize) {
                output.add(rawFieldText);
                break;
            }
            Integer split = rawFieldText.substring(0, maxSize).lastIndexOf("\n");
            output.add(rawFieldText.substring(0, split));
            rawFieldText = rawFieldText.substring(split+1);
        }

        return output;
    }

    private List<List<DiscordEmbedField>> getResultPages(List<DiscordEmbedField> embedFields) {
        List<List<DiscordEmbedField>> pages = new ArrayList<>();

        List<DiscordEmbedField> currentPage = new ArrayList<>();

        for (int i=0; i<embedFields.size(); i++) {
            DiscordEmbedField field = embedFields.get(i);
            if (currentPage.stream().mapToInt(DiscordEmbedField::getLength).sum() + field.getLength() > MAX_PAYLOAD_SIZE) {
                pages.add(currentPage);
                currentPage = new ArrayList<>();
                currentPage.add(field);
            } else {
                currentPage.add(field);
            }
        }

        pages.add(currentPage);

        return pages;
    }
}