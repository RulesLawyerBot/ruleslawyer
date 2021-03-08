package chat_platform;

import contract.searchResults.SearchResult;
import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import repository.SearchRepository;

import java.util.List;

import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static java.lang.String.join;

public class ChatMessageService {

    public String getQuery(String message) {
        int indexLeft = message.indexOf("{{");
        int indexRight = message.indexOf("}}");
        if (indexLeft == -1 || indexRight == -1 || indexRight < indexLeft)
            return "";
        return message.substring(indexLeft+2, indexRight).toLowerCase();
    }

    public String getQueryForNextPage(RuleSearchRequest ruleSearchRequest) {
        String keywordsString = join("|", ruleSearchRequest.getKeywords());
        String pageString = "p" + (ruleSearchRequest.getPageNumber()+1);
        if (ruleSearchRequest.getRuleSource() == ANY_DOCUMENT) {
            return "{{" + keywordsString + "|" + pageString + "}}";
        }
        return "{{" + keywordsString + "|" + ruleSearchRequest.getRuleSource().toString() + "|" + pageString + "}}";
    }
}
