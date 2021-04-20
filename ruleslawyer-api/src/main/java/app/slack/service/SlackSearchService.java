package app.slack.service;

import org.springframework.stereotype.Service;
import service.RawRuleSearchService;

import java.util.Map;

@Service
public class SlackSearchService {

    private RawRuleSearchService rawRuleSearchService;

    public SlackSearchService() {
        this.rawRuleSearchService = new RawRuleSearchService();
    }
}
