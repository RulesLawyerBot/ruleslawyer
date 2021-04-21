package app.slack.controller;

import app.slack.contract.SlackResponse;
import app.slack.service.SlackSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/slack")
public class SlackController {

    @Autowired
    private SlackSearchService slackSearchService;

    @RequestMapping(
            value="/rule", method = POST,
            consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE)
    public SlackResponse getRule(
            @RequestParam Map<String, String> body
    ) {
        body.forEach((k, v) -> {
            System.out.println(k + " | " + v);
        });

        return slackSearchService.searchRules(body.get("text"));
    }
}
