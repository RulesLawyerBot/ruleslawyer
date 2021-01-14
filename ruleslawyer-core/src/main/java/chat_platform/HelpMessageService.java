package chat_platform;

import java.util.HashMap;

public class HelpMessageService {

    public static final String MAIN_HELP = "**RulesLawyer** is a discord bot that answers rules questions related to Magic: The Gathering. After reading this post, feel free to delete it by clicking the reaction. You can bring up this post again by doing {{help}}.\n" +
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
            "**Filtering**: Filter results by including the rule source after a pipe character like this: {{serious problems|JAR}}\n" +
            "\n" +
            "**Pagination**: Paginate by including the page number after a pipe character like this: {{echo|p1}}. The first (default) page is page zero.\n" +
            "\n" +
            "**A note**: RulesLawyer has the capacity to determine which rules are most likely to be the ones you want, rather than just giving you random ones that match the search query. However, as with any relevancy algorithm, its an inexact science. The algorithm chooses which results to return based on relevancy, but uses a different ordering to determine what order to display the results within that page. So the result you want is unlikely to be the first result given.";

    public static final String HELP_ABOUT = "**About RulesLawyer**\n" +
            "\n" +
            "RulesLawyer is written by Elaine Cao, Level 2 from Calgary, Alberta.\n" +
            "\n" +
            "RulesLawyer uses material that is copyrighted to Wizards of the Coast and is used under fair use doctrine. Feel free to do whatever with it as long as you credit me.\n" +
            "\n" +
            "Contact me with bugs, feedback, and other inquiries at @ RulesLawyerBot on Twitter or Oritart#2698 on Discord.";

    public static final String HELP_ADD = "Add **RulesLawyer** to your own server by using this link: https://discordapp.com/oauth2/authorize?client_id=590184543684788253&scope=bot&permissions=346112";

    public static final String HELP_DEV = "```\n" +
            "--RulesLawyer patch notes--\n" +
            "Current rules version: Commander Legends\n" +
            "Last updated: 14-01-2021\n" +
            "--v1.7.2--\n" +
            "Some minor performance improvements.\n" +
            "--v1.7.1--\n" +
            "I have Intents permissions again! Message deletion is back, as well as some back-end logging things.\n" +
            "Just wanted to quickly patch this while some other improvements are still being worked on.\n" +
            "--Last major update: v1.7.0--\n" +
            "Mana symbol emojis! check it out by calling, for example, {{mana symbol}}\n" +
            "Some rule display changes that should increase clarity.\n" +
            "Some deployment changes to make it easier to patch in the future.\n" +
            "BUT MOSTLY MANA SYMBOLS ITS SO PRETTY\n" +
            "--Future development and known issues--\n" +
            "At some point I will get around to digital events rules.\n" +
            "Fuzzy searching is being explored.\n" +
            "Android version is actively being worked on. Slack is (still) pending me finding a deployment model that actually works with their API and isn't expensive (read: probably going to sit indefinitely)\n" +
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
