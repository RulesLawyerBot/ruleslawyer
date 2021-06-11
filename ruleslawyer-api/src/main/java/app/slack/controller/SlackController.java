package app.slack.controller;

import app.slack.contract.SlackBlock;
import app.slack.contract.SlackField;
import app.slack.contract.SlackResponse;
import app.slack.service.SlackAuthenticationService;
import app.slack.service.SlackSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static app.slack.service.SlackHelpService.SLACK_MAIN_HELP;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/slack")
public class SlackController {

    @Autowired
    private SlackSearchService slackSearchService;

    @Autowired
    private SlackAuthenticationService slackAuthenticationService;

    @RequestMapping(
            value="/rule", method = POST,
            consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE
    )
    public SlackResponse getRule(
            @RequestParam Map<String, String> body
    ) {
        body.forEach((k, v) -> {
            System.out.println(k + " | " + v);
        });

        return slackSearchService.searchRules(body.get("text"));
    }

    @RequestMapping(
            value="/help", method = POST,
            consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE
    )
    public SlackResponse getHelp(
            @RequestParam Map<String, String> body
    ) {
        return new SlackResponse(
                "in_channel",
                singletonList(
                        new SlackBlock(
                                "section",
                                singletonList(
                                        new SlackField(
                                                "mrkdwn",
                                                SLACK_MAIN_HELP
                                        )
                                ))
                ),
                emptyList()
        );
    }

    @RequestMapping(
            value="/auth", method = GET
    )
    public String authenticate(
            @RequestParam(value="code") String code
    ) {
        try {
            slackAuthenticationService.authenticate(code);
            return "Bot should be installed now";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in authentication";
        }
    }
}
