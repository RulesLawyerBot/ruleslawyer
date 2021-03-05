package search;

import chat_platform.ChatMessageService;
import chat_platform.HelpMessageService;
import contract.RuleSource;
import contract.searchResults.SearchResult;
import contract.rules.AbstractRule;
import contract.rules.Rule;
import contract.rules.RuleHeader;
import contract.rules.RuleSubheader;
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
import static java.lang.String.format;
import static java.lang.String.join;
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
        return getSearchResult(discordSearchRequest);
    }

    public DiscordSearchResult getSearchResult(DiscordSearchRequest discordSearchRequest) {
        List<SearchResult<AbstractRule>> rawResults = chatMessageService.processMessage(discordSearchRequest);

        if (rawResults.size() == 0) {
            return new DiscordSearchResult(
                    new EmbedBuilderBuilder()
                            .setAuthor("RulesLawyer")
                            .addFields(singletonList(new DiscordEmbedField(getEmbedTitle(discordSearchRequest), "No Results Found")))
                            .build()
            );
        }

        EmbedBuilderBuilder result = new EmbedBuilderBuilder()
                .setAuthor("RulesLawyer").setTitle(getEmbedTitle(discordSearchRequest));

        List<DiscordEmbedField> embedFields = rawResults.stream()
                .map(this::getFieldsForRawResult)
                .flatMap(Collection::stream)
                .collect(toList());
        List<List<DiscordEmbedField>> pages = splitResultPages(embedFields);

        Integer moddedPageNumber = (discordSearchRequest.getPageNumber() + pages.size()) % pages.size();
        result.addFields(pages.get(moddedPageNumber));

        String footer = format("Requested by: %s | page %s of %s | Use arrow keys for pagination",
                discordSearchRequest.getRequester(), moddedPageNumber+1, pages.size());

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
                        //TODO still gotta do something with this
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

    private String getEmbedTitle(DiscordSearchRequest discordSearchRequest) {
        return discordSearchRequest.getRuleSource() == RuleSource.ANY ?
                join("/", discordSearchRequest.getKeywords()) :
                discordSearchRequest.getRuleSource() + " | " + join("/", discordSearchRequest.getKeywords());
    }

    private List<DiscordEmbedField> getFieldsForRawResult(SearchResult<AbstractRule> result) {
        AbstractRule rule = result.getEntry();

        List<DiscordEmbedField> results = null;
        if (rule.getClass() == Rule.class) {
            results = getFieldForBaseRule((Rule) rule);
        } else if (rule.getClass() == RuleSubheader.class) {
            results = getFieldForRuleSubheader((RuleSubheader) rule);
        } else if (rule.getClass() == RuleHeader.class) {
            results = getFieldForRuleHeader((RuleHeader) rule);
        }
        assert results != null; // ehh its fine
        results.forEach(field -> field.setRelevancy(result.getRelevancy()));
        return results;
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
            if (fieldNameRemainder.length() + rawFieldText.length() < MAX_FIELD_VALUE_SIZE-1) {
                return singletonList(new DiscordEmbedField(fieldName, fieldNameRemainder + "\n" + rawFieldText));
            } else {
                return getSplitDiscordEmbedFields(fieldNameRemainder + "\n" + rawFieldText, fieldName);
            }
        }
        return getSplitDiscordEmbedFields(rawFieldText, rawFieldName);
    }

    private List<DiscordEmbedField> getSplitDiscordEmbedFields(String rawFieldText, String fieldName) {
        List<String> splitFieldText = splitFieldText(rawFieldText, MAX_FIELD_VALUE_SIZE);
        if (fieldName.length() < 128) {
            return splitFieldText.stream()
                    .map(fieldText -> new DiscordEmbedField(fieldName, fieldText))
                    .collect(toList()
                    );
        } else {
            List<DiscordEmbedField> embedFields = new ArrayList<>();
            embedFields.add(new DiscordEmbedField(fieldName, splitFieldText.get(0)));
            splitFieldText.remove(0);
            embedFields.addAll(splitFieldText.stream()
                    .map(text -> new DiscordEmbedField(fieldName.substring(0, 9), text))
                    .collect(toList())
            );
            return embedFields;
        }
    }

    private List<String> splitFieldText(String rawFieldText, Integer maxSize) {
        ArrayList<String> output = new ArrayList<>();

        while (true) {
            if (rawFieldText.length() < maxSize) {
                output.add(rawFieldText);
                break;
            }
            Integer split = rawFieldText.substring(0, maxSize).lastIndexOf("\n");
            if (split == -1) {
                split = rawFieldText.substring(0, maxSize).lastIndexOf(" ");
            }
            output.add(rawFieldText.substring(0, split));
            rawFieldText = rawFieldText.substring(split+1);
        }

        return output;
    }

    private List<List<DiscordEmbedField>> splitResultPages(List<DiscordEmbedField> embedFields) {
        List<List<DiscordEmbedField>> pages = new ArrayList<>();

        List<DiscordEmbedField> currentPage = new ArrayList<>();

        for (int i=0; i<embedFields.size(); i++) {
            DiscordEmbedField field = embedFields.get(i);
            if (currentPage.stream().mapToInt(DiscordEmbedField::getLength).sum() + field.getLength() > MAX_PAYLOAD_SIZE ||
                    (i > 0 && field.getRelevancy()-embedFields.get(i-1).getRelevancy() > 3000)) {
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
