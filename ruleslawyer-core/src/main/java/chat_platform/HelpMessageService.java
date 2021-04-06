package chat_platform;

import java.util.HashMap;

public class HelpMessageService {

    public static final String MAIN_HELP = "Hello!\n" +
            "\n" +
            "RulesLawyer is a discord bot that answers rules questions related to Magic: The Gathering. After reading this post, feel free to delete it by clicking the reaction. You can bring up this post again by doing {{help}}.\n" +
            "\n" +
            "RulesLawyer is designed to be used like a search engine, where you can not only call it with specific citations, but with search terms. Call it with curly braces {{like this}}, roughly analogous to Scryfall.\n" +
            "\n" +
            "For example, try the following searches:\n" +
            "{{cheating}}\n" +
            "{{failure to maintain definition}}\n" +
            "{{two-headed giant poison}}\n" +
            "{{\"attacks and isn't blocked\"}} (where the quotation marks forces it to search for an exact match instead of searching for each word individually)\n" +
            "\n" +
            "Read more: {{help|advanced}} for advanced usage, {{help|add}} for how you can add this to your own server, {{help|about}} for contact information or to report a bug, and {{help|dev}} for the most recent patch notes.";

    public static final String HELP_ADVANCED = "**RulesLawyer: advanced usage**\n" +
            "\n" +
            "**Filtering**: Filter results by including the rule source after a pipe character like this: {{serious problems|JAR}} You can also add additional keywords in any order.\n" +
            "\n" +
            "**Pagination**: Paginate by including the page number after a pipe character like this: {{echo|p1}}.\n" +
            "\n" +
            "**Reactions**: Currently, only the user who originally requested the information can use reactions. Reactions are available for pagination, changing between paper and digital-only rules, and deleting the message.\n" +
            "\n" +
            "**A note**: RulesLawyer has the capacity to determine which rules are most likely to be the ones you want, rather than just giving you random ones that match the search query. However, as with any relevancy algorithm, its an inexact science. The algorithm chooses which results to return based on relevancy, but uses a different ordering to determine what order to display the results within that page. Its complicated but its what makes the most sense.";

    public static final String HELP_ABOUT = "**About RulesLawyer**\n" +
            "\n" +
            "RulesLawyer is written by Elaine Cao, Level 2 from Calgary, Alberta.\n" +
            "\n" +
            "RulesLawyer uses material that is copyrighted to Wizards of the Coast and is used under fair use doctrine. Feel free to do whatever with it as long as you credit me.\n" +
            "\n" +
            "Contact me with bugs, feedback, and other inquiries at @ RulesLawyerBot on Twitter or Oritart#2698 on Discord.";

    public static final String HELP_ADD = "Add **RulesLawyer** to your own server by using this link: https://discordapp.com/oauth2/authorize?client_id=590184543684788253&scope=bot&permissions=2147838016";

    public static final String HELP_DEV = "```\n" +
            "--RulesLawyer patch notes--\n" +
            "Current rules version: Kaldheim\n" +
            "Last updated: 06-04-2021\n" +
            "--v1.9.3--\n" +
            "New api endpoint for Slack and for a webapp. In progress!\n" +
            "Bump Javacord to 3.2.0." +
            "With this change, RulesLawyer now replies to the requesting message.\n" +
            "--Last major patch: v1.9.0--\n" +
            "Fixed various bugs related to pagination.\n" +
            "Digital events rules are in! Let me know if there are any parsing issues.\n" +
            "--Future development and known issues--\n" +
            "Strixhaven will be added as 1.9.4 once the set is released.\n" +
            "Slash/application commands will be added once javacord adds support for it.\n" +
            "Enough people have requested card search that I'm considering just adding it.\n" +
            "Fuzzy searching is being explored but ITS HARD TO MAKE WORK OKAY\n" +
            "Android version is actively being worked on.\n" +
            "Slack is in progress BUT WHY DO THEY GOTTA MAKE IT SO HARD\n" +
            "```";

    public static final String NOT_FOUND = "I don't have that help file. Use {{help}} for the main help file.";

    private HashMap<String, String> helpFiles;

    public HelpMessageService() {
        helpFiles = new HashMap<>();
        helpFiles.put("advanced", HELP_ADVANCED);
        helpFiles.put("add", HELP_ADD);
        helpFiles.put("about", HELP_ABOUT);
        helpFiles.put("dev", HELP_DEV);
    }

    public String getHelpFile() {
        return MAIN_HELP;
    }

    public String getHelpFile(String query) {
        return helpFiles.getOrDefault(query, NOT_FOUND);
    }
}
