package chat_platform;

import contract.SearchResult;
import contract.rules.AbstractRule;
import contract.rules.Rule;
import contract.rules.RuleHeader;
import contract.searchRequests.RuleSearchRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import repository.SearchRepository;

import java.util.List;
import java.util.Optional;

import static contract.RequestSource.DISCORD;
import static contract.RuleSource.IPG;
import static contract.searchRequests.builder.RuleSearchRequestBuilder.aRuleSearchRequest;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ChatMessageServiceTest {

    ChatMessageService chatMessageService;

    @Mock
    SearchRepository<AbstractRule> searchRepository;

    @Before
    public void setUp() {
        chatMessageService = new ChatMessageService(DISCORD, searchRepository);
    }

    @Test
    public void queryWithNoParameters() {
        String query = "{{cheating}}";
        RuleSearchRequest passThroughRequest = aRuleSearchRequest()
                .appendKeywords(singletonList("cheating"))
                .build();
        String ruleText = "cheating is bad";
        String expectedResult = "**IPG cheating is bad**\n";
        RuleHeader returnedRule = new RuleHeader(ruleText, IPG);
        when(searchRepository.getSearchResult(passThroughRequest))
                .thenReturn(singletonList(new SearchResult<>(returnedRule, 0)));

        List<String> output = chatMessageService.processMessage(query);

        assertThat(output.size(), is(1));
        assertThat(output.get(0), is(expectedResult));
    }

    @Test
    public void queryWithOtherText() {
        String query = "Is {{cheating}} okay?";
        RuleSearchRequest passThroughRequest = aRuleSearchRequest()
                .appendKeywords(singletonList("cheating"))
                .build();
        String ruleText = "cheating is bad";
        String expectedResult = "**IPG cheating is bad**\n";
        RuleHeader returnedRule = new RuleHeader(ruleText, IPG);
        when(searchRepository.getSearchResult(passThroughRequest))
                .thenReturn(singletonList(new SearchResult<>(returnedRule, 0)));

        List<String> output = chatMessageService.processMessage(query);

        assertThat(output.size(), is(1));
        assertThat(output.get(0), is(expectedResult));
    }

    @Test
    public void queryWithRuleSource() {
        String query = "{{cheating|IPG}}";
        RuleSearchRequest passThroughRequest = aRuleSearchRequest()
                .appendKeywords(singletonList("cheating"))
                .setRuleSource(IPG)
                .build();
        String ruleText = "cheating is bad";
        String expectedResult = "**IPG cheating is bad**\n";
        RuleHeader returnedRule = new RuleHeader(ruleText, IPG);
        when(searchRepository.getSearchResult(passThroughRequest))
                .thenReturn(singletonList(new SearchResult<>(returnedRule, 0)));

        List<String> output = chatMessageService.processMessage(query);

        assertThat(output.size(), is(1));
        assertThat(output.get(0), is(expectedResult));
    }

    @Test
    public void queryWithPagination() {
        String query = "{{cheating|p2}}";
        RuleSearchRequest passThroughRequest = aRuleSearchRequest()
                .appendKeywords(singletonList("cheating"))
                .setPageNumber(2)
                .build();
        String ruleText = "cheating is bad";
        String expectedResult = "**IPG cheating is bad**\n";
        RuleHeader returnedRule = new RuleHeader(ruleText, IPG);
        when(searchRepository.getSearchResult(passThroughRequest))
                .thenReturn(singletonList(new SearchResult<>(returnedRule, 0)));

        List<String> output = chatMessageService.processMessage(query);

        assertThat(output.size(), is(1));
        assertThat(output.get(0), is(expectedResult));
    }

    @Test
    public void queryWithExactParameters() {
        //TODO
    }

    @Test
    public void notAQuery_DontDoAnything() {
        //TODO
    }
}
