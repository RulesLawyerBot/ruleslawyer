package search;

import chat_platform.HelpMessageService;
import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import contract.searchRequests.RuleSearchRequest;
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

import static contract.rules.enums.RuleRequestCategory.*;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static search.contract.builder.DiscordSearchRequestBuilder.aDiscordSearchRequest;
import static search.contract.builder.DiscordSearchRequestBuilder.fromDiscordSearchRequest;

public class DiscordRuleSearchService {

    private HelpMessageService helpMessageService;
    private SearchRepository<AbstractRule> paperRuleSearchRepository;
    private SearchRepository<AbstractRule> digitalRuleSearchRepository;

    public static final Integer MAX_PAYLOAD_SIZE = 3000;
    public static final Integer MAX_FIELD_NAME_SIZE = 256;
    public static final Integer MAX_FIELD_VALUE_SIZE = 1024;

    public DiscordRuleSearchService(SearchRepository<AbstractRule> paperRuleSearchRepository, SearchRepository<AbstractRule> digitalRuleSearchRepository) {
        this.helpMessageService = new HelpMessageService();
        this.paperRuleSearchRepository = paperRuleSearchRepository;
        this.digitalRuleSearchRepository = digitalRuleSearchRepository;
    }

    public DiscordSearchResult getSearchResult(String author, String text) {
        String query = getQuery(text);
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

    private String getQuery(String message) {
        int indexLeft = message.indexOf("{{");
        int indexRight = message.indexOf("}}");
        if (indexLeft == -1 || indexRight == -1 || indexRight < indexLeft)
            return "";
        return message.substring(indexLeft+2, indexRight).toLowerCase();
    }

    public DiscordSearchResult getSearchResult(DiscordSearchRequest discordSearchRequest) {
        List<List<SearchResult<AbstractRule>>> rawResults = processMessage(discordSearchRequest);

        if (rawResults.get(0).size() == 0) {
            if (rawResults.get(1).size() > 0) {
                if (discordSearchRequest.getRuleRequestCategory() == PAPER) {
                    return getSearchResult(
                            fromDiscordSearchRequest(discordSearchRequest)
                                    .setRuleRequestCategory(DIGITAL)
                                    .build()
                    );
                } else {
                    return getSearchResult(
                            fromDiscordSearchRequest(discordSearchRequest)
                                    .setRuleRequestCategory(PAPER)
                                    .build()
                    );
                }
            } else {
                return new DiscordSearchResult(
                        new EmbedBuilderBuilder()
                                .setAuthor("RulesLawyer")
                                .addFields(singletonList(new DiscordEmbedField(getEmbedTitle(discordSearchRequest), "No Results Found")))
                                .build()
                );
            }
        }

        EmbedBuilderBuilder result = new EmbedBuilderBuilder()
                .setAuthor("RulesLawyer").setTitle(getEmbedTitle(discordSearchRequest));

        List<DiscordEmbedField> embedFields = rawResults.get(0).stream()
                .map(this::getFieldsForRawResult)
                .flatMap(Collection::stream)
                .collect(toList());
        List<List<DiscordEmbedField>> pages = splitResultPages(embedFields);

        Integer moddedPageNumber = (discordSearchRequest.getPageNumber() + pages.size()) % pages.size();
        result.addFields(pages.get(moddedPageNumber));

        String footer = getFooter(
                discordSearchRequest.getRequester(),
                moddedPageNumber+1,
                pages.size(),
                rawResults.get(1).size() == 0 ? ANY_RULE_TYPE : discordSearchRequest.getRuleRequestCategory()
        );

        result.setFooter(footer);

        return new DiscordSearchResult(result.build());

    }

    private List<List<SearchResult<AbstractRule>>> processMessage(RuleSearchRequest searchRequest) {
        return searchRequest.getRuleRequestCategory() == DIGITAL?
                asList(
                        digitalRuleSearchRepository.getSearchResult(searchRequest),
                        paperRuleSearchRepository.getSearchResult(searchRequest)
                ) :
                asList(
                        paperRuleSearchRepository.getSearchResult(searchRequest),
                        digitalRuleSearchRepository.getSearchResult(searchRequest)
                );
    }

    private String getFooter(String requester, Integer pageNumber, Integer pageSize, RuleRequestCategory availableRuleSwap) {
        if (availableRuleSwap == DIGITAL) {
            return format("Requested by: %s | page %s of %s | paper rules available | Use arrow reactions for pagination",
                    requester, pageNumber, pageSize);
        }
        if (availableRuleSwap == PAPER) {
            return format("Requested by: %s | page %s of %s | digital rules available | Use arrow reactions for pagination",
                    requester, pageNumber, pageSize);
        }
        return format("Requested by: %s | page %s of %s | Use arrow reactions for pagination",
                requester, pageNumber, pageSize);
    }

    private DiscordSearchRequest getSearchRequest(String author, String query) {
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

    private String getEmbedTitle(DiscordSearchRequest discordSearchRequest) {
        String output = join("/", discordSearchRequest.getKeywords());
        if (discordSearchRequest.getRuleSource() != ANY_DOCUMENT) {
            output = discordSearchRequest.getRuleSource() + " | " + output;
        }
        if (discordSearchRequest.getRuleRequestCategory() != ANY_RULE_TYPE) {
            output = discordSearchRequest.getRuleRequestCategory().toString().toLowerCase() + " | " + output;
        }
        return output;
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
