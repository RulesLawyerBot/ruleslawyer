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
                                    "RulesLawyer is designed to be used like a search engine, where you can not only call it with specific citations, but with search terms. You can query rules with \"/rule\" and cards with \"/card\"."
                    ),
                    new DiscordEmbedField(
                            "About the search algorithm",
                            "RulesLawyer Rule Search not only searches the Comprehensive Rules (CR), but also the Infraction Procedure Guide (IPG), Magic Tournament Rules (MTR), and many other documents. However, it will only find search terms that use words that are actually in the rules documents, so its recommended to not use extra \"filler\" words while searching. Card Search searches for card name and card oracle in any combination.\n" +
                                    "\n" +
                                    "RulesLawyer uses fuzzy searching and relevancy heuristics so that the result you most likely want appears first, but this is an inexact science and not a guarantee. If there are too many results, it is suggested that you re-run the search with additional parameters.\n" +
                                    "\n" +
                                    "RulesLawyer will sometimes try to autocomplete your query, but this is experimental and may sometimes result in weirdness. Much like a traditional search engine, you do not have to select the autocomplete suggestions and should feel free to submit whatever query you want."
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
                                    "More help files are at \"/help add\" to add this to your own server, \"/help about\" for contact information, or \"/help dev\" for the latest patch notes.\n"
                    ),
                    new DiscordEmbedField(
                            "About RulesLawyer",
                            "RulesLawyer is written by Elaine Cao, Level 2 from Montreal, Quebec.\n" +
                                    "\n" +
                                    "RulesLawyer uses material that is copyrighted to Wizards of the Coast and is used under fair use doctrine. Feel free to do whatever with it as long as you credit me.\n" +
                                    "\n" +
                                    "Contact me with bugs, feedback, and other inquiries at @ RulesLawyerBot on Twitter or Oritart#0001 on Discord."
                    ),
                    new DiscordEmbedField(
                            "Support us!",
                            "RulesLawyer has a Patreon, located at https://patreon.com/ruleslawyer. This helps us pay for server costs."
                    )
            );
    public static final EmbedBuilderBuilder HELP_ABOUT_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "About RulesLawyer",
                            "RulesLawyer is written by Elaine Cao, Level 2 from Montreal, Quebec.\n" +
                                    "\n" +
                                    "RulesLawyer uses material that is copyrighted to Wizards of the Coast and is used under fair use doctrine. Feel free to do whatever with it as long as you credit me.\n" +
                                    "\n" +
                                    "Contact me with bugs, feedback, and other inquiries at @ RulesLawyerBot on Twitter or Oritart#0001 on Discord."
                    ),
                    new DiscordEmbedField(
                            "Support us!",
                            "RulesLawyer has a Patreon, located at https://patreon.com/ruleslawyer. This helps us pay for server costs."
                    )
            );
    public static final EmbedBuilderBuilder HELP_ADD_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "Add RulesLawyer to your own server",
                            "Add **RulesLawyer** to your own server by using this link: https://discordapp.com/oauth2/authorize?client_id=590184543684788253&scope=bot&permissions=2147838016&scope=applications.commands%20bot"
                    )
            );
    public static final EmbedBuilderBuilder HELP_DEV_EMBED = new EmbedBuilderBuilder()
            .addFields(

                    new DiscordEmbedField(
                            "RulesLawyer patch notes",
                            "Last updated: 2023-08-29\n" +
                                    "Rules version: March of the MachineL Aftermath\n" +
                                    "Cards version: Lord of the Rings"
                    ),
                    new DiscordEmbedField(
                            "v1.14.7",
                            "Updates for WOE."
                    ),
                    new DiscordEmbedField(
                            "v1.14.6",
                            "Card updates for LTR."
                    ),
                    new DiscordEmbedField(
                            "v1.14.5",
                            "CR and card updates for MOM."
                    ),
                    new DiscordEmbedField(
                            "v1.14.4",
                            "CR updates for ONE."
                    ),
                    new DiscordEmbedField(
                            "v1.14.3",
                            "Updated cards to include All Will Be One.\n" +
                                    "CR updates for ONE will be out when WotC publishes them."
                    ),
                    new DiscordEmbedField(
                            "v1.14.2",
                            "Updated cards to include Brother's War.\n" +
                                    "CR updates for Brother's War will be out when WotC publishes them."
                    ),
                    new DiscordEmbedField(
                            "v1.14.1",
                            "Updated rules to UNF."
                    ),
                    new DiscordEmbedField(
                            "v1.14.0",
                            "Upgraded cards to include Unfinity.\n" +
                                    "Display improvements! Hopefully results are less noisy now.\n" +
                                    "Did a pass over digital rules and cleaned up parsing errors."
                    ),
                    new DiscordEmbedField(
                            "Future Development",
                                    "Localization of slash commands into non-English languages, and support for non-English characters.\n" +
                                    "Slack and Android versions on hold for now, though its one of the Patreon goals (www.patreon.com/ruleslawyer)\n" +
                                    "Known issues: Some foil prices not displaying, displaying foil set art instead of normal art"
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
