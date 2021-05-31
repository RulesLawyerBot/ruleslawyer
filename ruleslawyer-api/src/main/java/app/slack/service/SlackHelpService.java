package app.slack.service;

public class SlackHelpService {

    public static final String SLACK_MAIN_HELP = "Welcome to RulesLawyer: Slack edition! This is a closed alpha. Please give feedback to Elaine Cao / Oritart.\n" +
            "\n" +
            "Interactions with RulesLawyer on Slack take place in application (slash) commands. Use /rule <parameters> to search RulesLawyer's rule database, or /rl-help to bring up this help file. Discord's calling method (with {{brackets}}) doesn't work here, for now.\n" +
            "\n" +
            "If a query results in a \"timeout\" error, try re-sending the query after a few seconds. For now the application is running on a free Heroku dyno, which will be upgraded once we're actually ready for public use. Any other error messages or strange output should be reported, along with what query caused the strange message.\n" +
            "\n" +
            "In coming weeks I'll be making some changes to the way that output is displayed to try to get the Slack version up to the functionality of Discord, but for now it will at least respond to simple queries.";
}
