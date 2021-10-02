package search;

import chat_platform.rule_output.OutputFieldSplitService;
import contract.rules.*;
import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import contract.searchResults.RawRuleSearchResult;
import contract.searchResults.SearchResult;
import init_utils.ManaEmojiService;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import search.contract.DiscordEmbedField;
import search.contract.request.DiscordRuleSearchRequest;
import search.contract.DiscordReturnPayload;
import search.contract.EmbedBuilderBuilder;
import search.contract.request.builder.DiscordSearchRequestBuilder;
import service.HelpMessageSearchService;
import service.RawRuleSearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static contract.rules.enums.RuleRequestCategory.*;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static ingestion.rule.JsonRuleIngestionService.getRawDigitalRulesData;
import static ingestion.rule.JsonRuleIngestionService.getRawRulesData;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static search.contract.request.builder.DiscordSearchRequestBuilder.aDiscordSearchRequest;
import static search.interaction_pagination.InteractionPaginationStatics.*;

public class DiscordRuleSearchService {

    private HelpMessageSearchService helpMessageSearchService;
    private RawRuleSearchService rawRuleSearchService;
    private OutputFieldSplitService outputFieldSplitService;

    public static final String RULE_SEARCH_AUTHOR_TEXT = "RulesLawyer Rules Search";
    public static final Integer MAX_PAYLOAD_SIZE = 3000;
    public static final Integer MAX_FIELD_NAME_SIZE = 256;
    public static final Integer MAX_FIELD_VALUE_SIZE = 1024;
    public static final String NO_RESULTS_FOUND_HELP_MESSAGE = "RulesLawyer searches the following rules documents: the Comprehensive Rules, Infraction Procedure Guide, Magic Tournament Rules, Judging at Regular document, Oathbreaker rules, Digital Infraction Procedure Guide, and Digital Magic Tournament rules.\n" +
            "Much like a traditional search engine, if your query does not appear in these documents, no results will be returned. It is suggested that you only use words that you think are likely to appear in these documents.\n" +
            "For additional help, do /help.";

    public DiscordRuleSearchService(ManaEmojiService manaEmojiService) {
        List<AbstractRule> rules = getRawRulesData().stream()
                .map(manaEmojiService::replaceManaSymbols)
                .collect(toList());
        List<AbstractRule> digitalRules = getRawDigitalRulesData().stream()
                .map(manaEmojiService::replaceManaSymbols)
                .collect(toList());
        this.helpMessageSearchService = new HelpMessageSearchService();
        this.rawRuleSearchService = new RawRuleSearchService(rules, digitalRules);
        this.outputFieldSplitService = new OutputFieldSplitService(MAX_FIELD_NAME_SIZE, MAX_FIELD_VALUE_SIZE);
    }

    public DiscordReturnPayload getSearchResult(String author, String text) {
        String query = getQuery(text);
        return getSearchResultFromPlainQuery(author, query);
    }

    public DiscordReturnPayload getSearchResultFromPlainQuery(String author, String query) {
        if (query.equals(""))
            return null;

        if (query.startsWith("help")) {
            return query.equals("help") ?
                    new DiscordReturnPayload(helpMessageSearchService.getHelpFile()).setComponents(DELETE_ONLY_ROW) :
                    new DiscordReturnPayload(helpMessageSearchService.getHelpFile(query.substring(5))).setComponents(DELETE_ONLY_ROW);
        }

        DiscordRuleSearchRequest discordRuleSearchRequest = getSearchRequest(author, query);
        return getSearchResult(discordRuleSearchRequest);
    }

    private String getQuery(String message) {
        int indexLeft = message.indexOf("{{");
        int indexRight = message.indexOf("}}");
        if (indexLeft == -1 || indexRight == -1 || indexRight < indexLeft)
            return "";
        return message.substring(indexLeft+2, indexRight).toLowerCase();
    }

    public DiscordReturnPayload getSearchResult(DiscordRuleSearchRequest discordRuleSearchRequest) {
        RawRuleSearchResult rawResults = rawRuleSearchService.getRawResult(discordRuleSearchRequest);

        if (rawResults.getRawResults().size() == 0) {
            return new DiscordReturnPayload(
                    new EmbedBuilderBuilder()
                            .setAuthor("RulesLawyer")
                            .addFields(asList(
                                    new DiscordEmbedField(getEmbedTitle(discordRuleSearchRequest), "No Results Found"),
                                    new DiscordEmbedField("Quick help", NO_RESULTS_FOUND_HELP_MESSAGE)
                            ))
            )
                    .setComponents(DELETE_ONLY_ROW);
        }

        EmbedBuilderBuilder result = new EmbedBuilderBuilder()
                .setAuthor(RULE_SEARCH_AUTHOR_TEXT)
                .setTitle(getEmbedTitle(discordRuleSearchRequest));

        List<DiscordEmbedField> embedFields = rawResults.getRawResults().stream()
                .map(this::getFieldsForRawResult)
                .flatMap(Collection::stream)
                .collect(toList());
        List<List<DiscordEmbedField>> pages = splitResultPages(embedFields);

        Integer moddedPageNumber = (discordRuleSearchRequest.getPageNumber() + pages.size()) % pages.size();
        result.addFields(pages.get(moddedPageNumber));

        String footer = getFooter(
                moddedPageNumber+1,
                pages.size(),
                rawResults.isFuzzy()
        );

        result.setFooter(footer);

        String webappUrl = getWebappURL(discordRuleSearchRequest.getKeywords());

        return new DiscordReturnPayload(result)
                .setContent(webappUrl)
                .setComponents(
                        rawResults.hasOtherCategory() ?
                                RULE_ROW_WITH_SOURCE_SWAP :
                                RULE_ROW_WITHOUT_SOURCE_SWAP,
                        ActionRow.of(Button.link(webappUrl, "View this search on ruleslawyer.app"))
                );
    }

    private String getWebappURL(List<String> query) {
        return "https://www.ruleslawyer.app/search?q=" + join("+", query).replace(" ", "+");
    }

    private String getFooter(
            Integer pageNumber,
            Integer pageSize,
            boolean isFuzzy
    ) {
        String baseFooter = format("page %s of %s", pageNumber, pageSize);
        return isFuzzy ?
                baseFooter + " | No exact match found. Automatically using experimental fuzzy search" :
                baseFooter;
    }

    private DiscordRuleSearchRequest getSearchRequest(String author, String query) {
        DiscordSearchRequestBuilder ruleSearchRequest = aDiscordSearchRequest().setRequester(author);
        List<String> commands = asList(query.split("\\|"));

        commands.forEach(
                command -> {
                    if(command.startsWith("p") && command.length() < 5) {
                        try {
                            ruleSearchRequest.setPageNumber(parseInt(command.substring(1)));
                        } catch (NumberFormatException ignored) {
                            ruleSearchRequest.appendKeywords(getKeywordsFromSubquery(command));
                        }
                    }
                    else {
                        try {
                            ruleSearchRequest.setRuleSource(RuleSource.valueOf(command.toUpperCase()));
                        } catch (IllegalArgumentException ignored) {
                            try {
                                ruleSearchRequest.setRuleRequestCategory(RuleRequestCategory.valueOf(command.toUpperCase()));
                            } catch (IllegalArgumentException alsoIgnored) {
                                ruleSearchRequest.appendKeywords(getKeywordsFromSubquery(command));
                            }
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

    private String getEmbedTitle(DiscordRuleSearchRequest discordRuleSearchRequest) {
        String output = join("/", discordRuleSearchRequest.getKeywords());
        if (discordRuleSearchRequest.getRuleSource() != ANY_DOCUMENT) {
            output = discordRuleSearchRequest.getRuleSource() + " | " + output;
        }
        if (discordRuleSearchRequest.getRuleRequestCategory() != ANY_RULE_TYPE) {
            output = discordRuleSearchRequest.getRuleRequestCategory().toString().toLowerCase() + " | " + output;
        }
        return output;
    }

    private List<DiscordEmbedField> getFieldsForRawResult(SearchResult<AbstractRule> result) {
        AbstractRule rule = result.getEntry();
        return rule.getPrintedRules().stream()
                .map(this::makeEmbedFieldsForRawText)
                .flatMap(Collection::stream)
                .peek(field -> field.setRelevancy(result.getRelevancy()))
                .collect(toList());
    }

    private List<DiscordEmbedField> makeEmbedFieldsForRawText(PrintableRule rule) {
        return outputFieldSplitService.getGenericRuleBlocks(rule).stream()
                .map(genericField -> new DiscordEmbedField(genericField.getHeader(), genericField.getBody()))
                .collect(toList());
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
