package app.api.controller;

import app.api.pojo.ApiNormalizedRule;
import app.api.pojo.ApiRulesPayload;
import app.api.service.ApiSearchService;
import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import contract.searchRequests.RuleSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static contract.rules.enums.RuleRequestCategory.ANY_RULE_TYPE;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api")
@CrossOrigin(maxAge = 3600)
public class ApiSearchController {

    @Autowired
    private ApiSearchService apiSearchService;

    @RequestMapping(value="/search", method = {GET, POST})
    public ApiRulesPayload search(
            @RequestParam(value="keywords", required = false) List<String> keywords,
            @RequestParam(value="ruleSource", required = false) RuleSource ruleSource,
            @RequestParam(value="ruleRequestCategory", required = false) RuleRequestCategory ruleRequestCategory
    ) {
        RuleSearchRequest ruleSearchRequest = getRuleSearchRequest(
                keywords.stream()
                    .map(String::toLowerCase)
                    .collect(toList()),
                ruleSource,
                ruleRequestCategory
        );
        return apiSearchService.getRuleSearchResults(ruleSearchRequest);
    }

    @RequestMapping(value="/citation", method = {GET, POST})
    public ApiNormalizedRule getCitation(
            @RequestParam(value="index") Integer index
    ) {
        return apiSearchService.getCitation(index);
    }

    private RuleSearchRequest getRuleSearchRequest(
            List<String> keywords,
            RuleSource ruleSource,
            RuleRequestCategory ruleRequestCategory
    ) {
        return new RuleSearchRequest(
                keywords == null || keywords.size() == 0 ? emptyList() : keywords,
                ruleSource == null ? ANY_DOCUMENT : ruleSource,
                0,
                ruleRequestCategory == null ? ANY_RULE_TYPE : ruleRequestCategory
        );
    }

    @RequestMapping(value="/index", method = {GET, POST})
    public List<ApiNormalizedRule> getTopLevelRules(
            @RequestParam(value="ruleSource", required = false) RuleSource ruleSource
    ) {
        return ruleSource == null ?
                apiSearchService.getRuleIndex() :
                apiSearchService.getRuleIndex(ruleSource);
    }

    @RequestMapping(value="/index/documents", method = {GET, POST})
    public List<RuleSource> getRuleSources() {
        return asList(RuleSource.values());
    }
}
