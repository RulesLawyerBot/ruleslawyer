package service;

import search.contract.DiscordEmbedField;
import search.contract.EmbedBuilderBuilder;

import java.util.HashMap;

public class HelpMessageSearchService {

    public static final String HELP_MAIN_IDENTIFIER = "main";
    public static final String HELP_ADD_IDENTIFIER = "add";
    public static final String HELP_DEV_IDENTIFIER = "dev";
    public static final String HELP_ABOUT_IDENTIFIER = "about";

    public static final EmbedBuilderBuilder MAIN_HELP_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "RulesLawyer Help",
                            "Hello!\n" +
                                    "\n" +
                                    "RulesLawyer is a Discord bot that helps judges and players answer rules questions related to Magic: The Gathering. After reading this post, feel free to delete it by pressing the button. You can bring this up again by doing /help (and selecting the command associated with RulesLawyer, if there's other options associated with other bots on this server).\n" +
                                    "\n" +
                                    "RulesLawyer is designed to be used like a search engine, where you can not only call it with specific citations, but with search terms. You can still call it with curly braces {{like this}}, roughly analogous to Scryfall, but it is recommended that you use the fancy new /rule command. You can also call up cards with /card."
                            ),
                    new DiscordEmbedField(
                            "About the search algorithm",
                            "RulesLawyer Rule Search not only searches the Comprehensive Rules (CR), but also the Infraction Procedure Guide (IPG), Magic Tournament Rules (MTR), and many other documents. However, it will only find search terms that use words that are actually in the rules documents, so its recommended to not use extra \"filler\" words while searching. Card Search searches for card name and card oracle in any combination.\n" +
                                    "\n" +
                                    "RulesLawyer uses fuzzy searching and relevancy heuristics so that the result you most likely want appears first, but this is an inexact science and not a guarantee. If there are too many results, it is suggested that you re-run the search with additional paramters."
                    ),
                    new DiscordEmbedField(
                            "For example, try the following searches:",
                            "/rule cheating\n" +
                                    "/rule failure to maintain definition\n" +
                                    "/rule two-headed giant poison\n" +
                                    "/rule dungeon\n" +
                                    "/rule \"attacks and isn't blocked\" (where the quotation marks forces it to search for an exact match instead of searching for each word individually)\n" +
                                    "/card thalia\n" +
                                    "/card cryptic (should bring up cryptic command instead of a random card with cryptic in the name)\n" +
                                    "\n" +
                                    "More help files are at \"/help add\" to add this to your own server, \"/help about\" for contact information, or \"/help dev\" for the latest patch notes.\n" +
                                    "\n" +
                                    "Card search is still fairly new and I would appreciate bugs being reported with \"/help about\""
                    )
            );
    public static final EmbedBuilderBuilder HELP_ABOUT_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "About RulesLawyer",
                            "RulesLawyer is written by Elaine Cao, Level 2 from Calgary, Alberta.\n" +
                                    "\n" +
                                    "RulesLawyer uses material that is copyrighted to Wizards of the Coast and is used under fair use doctrine. Feel free to do whatever with it as long as you credit me.\n" +
                                    "\n" +
                                    "Contact me with bugs, feedback, and other inquiries at @ RulesLawyerBot on Twitter or Oritart#0001 on Discord."
                    )
            );
    public static final EmbedBuilderBuilder HELP_ADD_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "Add RulesLawyer to your own server",
                            "Add **RulesLawyer** to your own server by using this link: https://discordapp.com/oauth2/authorize?client_id=590184543684788253&scope=bot&permissions=2147838016"
                    )
            );
    public static final EmbedBuilderBuilder HELP_DEV_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "RulesLawyer patch notes",
                            "Last updated: 2021-08-10\n" +
                                    "Rules version: Adventures in the Forgotten Realms"
                    ),
                    new DiscordEmbedField(
                            "v1.12.2",
                            "Some card relevancy algorithm changes."
                    ),
                    new DiscordEmbedField(
                            "v1.12.1",
                            "Fixed some bugs and confusion in card searching.\n" +
                                    "Updated: /card now supports pagination and exact matches (use \"quotation marks\" to exact match the title)"
                    ),
                    new DiscordEmbedField(
                            "v1.12.0",
                            "Card searching! /card is available.\n" +
                                    "www.ruleslawyer.app is now available! Search using the core RulesLawyer engine in a webapp with greater clarity.\n" +
                                    "Updated: /rule now supports rule source filtering (or skip the second argument to search everything)"
                    ),
                    new DiscordEmbedField(
                            "Future Development",
                            "Slack is being worked on, but still in progress.\n" +
                                    "Android version is on hold for now, though its one of the Patreon goals (www.patreon.com/ruleslawyer)"
                    )
            );
    public static final EmbedBuilderBuilder HELP_NOT_FOUND_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "Help file not found",
                            "I'm sorry, I don't have that help file. Use \"/help\" to get the main file. If you believe this is a bug, report it at \"/help dev\"."
                    )
            );

    private HashMap<String, EmbedBuilderBuilder> helpFiles;

    public HelpMessageSearchService() {
        helpFiles = new HashMap<>();
        helpFiles.put(HELP_MAIN_IDENTIFIER, MAIN_HELP_EMBED);
        helpFiles.put(HELP_ADD_IDENTIFIER, HELP_ADD_EMBED);
        helpFiles.put(HELP_ABOUT_IDENTIFIER, HELP_ABOUT_EMBED);
        helpFiles.put(HELP_DEV_IDENTIFIER, HELP_DEV_EMBED);
    }

    public EmbedBuilderBuilder getHelpFile() {
        return MAIN_HELP_EMBED;
    }

    public EmbedBuilderBuilder getHelpFile(String query) {
        return helpFiles.getOrDefault(query, HELP_NOT_FOUND_EMBED);
    }
}
