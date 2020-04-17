package chat_platform;

import java.util.HashMap;

public class HelpMessageService {

    public static final String MAIN_HELP = "**RulesLawyer** is a discord bot that answers rules questions related to Magic: The Gathering. After reading this post, feel free to delete it by clicking the check mark reaction. You can bring up this post again by doing {{help}}.\n" +
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
            "**Pagination**: Is in development, I promise.\n" +
            "\n" +
            "**A note**: RulesLawyer has the capacity to determine which rules are most likely to be the ones you want, rather than just giving you random ones that match the search query. However, as with any relevancy algorithm, its an inexact science. The algorithm chooses which results to return based on relevancy, but uses a different ordering to determine what order to display the results within that page. So the result you want is unlikely to be the first result given.";

    public static final String HELP_ABOUT = "**About RulesLawyer**\n" +
            "\n" +
            "RulesLawyer is written by Elaine Cao, Level 2 from Saint Louis, Missouri.\n" +
            "\n" +
            "RulesLawyer uses material that is copyrighted to Wizards of the Coast and is used under fair use doctrine. Feel free to do whatever with it as long as you credit me.\n" +
            "\n" +
            "Contact me with bugs, feedback, and other inquiries at @ RulesLawyerBot on Twitter or Oritart#2698 on Discord.";

    public static final String HELP_ADD = "Add **RulesLawyer** to your own server by using this link: https://discordapp.com/oauth2/authorize?client_id=590184543684788253&scope=bot&permissions=346112";

    public static final String HELP_DEV = "```\n" +
            "--RulesLawyer patch notes--\n" +
            "Current rules version: Ikoria, Lair of Behemoths\n" +
            "--Last updated: v1.4.0 4/12/2020--\n" +
            "Rules database updated for Ikoria." +
            "Pagination is finally finished! Please let me know if there are any glaring bugs I may have missed.\n" +
            "Pagination is zero-indexed (e.g. first page is page 0). If this drives you crazy, I apologize.\n" +
            "Redesigned relevancy algorithm in light of some unintended results. (Read: Search results are more likely to return what you want them to)\n" +
            "--Future development and known issues--\n" +
            "Some issues with the parser for the current version of the Magic Tournament Rules.\n" +
            "Some other parsing issues with unprintable characters. Blame WOTC for using weird symbols!\n" +
            "Fuzzy string matching?\n" +
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
