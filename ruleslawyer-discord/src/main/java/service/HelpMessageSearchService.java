package service;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import search.contract.DiscordEmbedField;
import search.contract.EmbedBuilderBuilder;

import java.util.HashMap;

public class HelpMessageSearchService {

    public static final String HELP_MAIN_IDENTIFIER = "main";
    public static final String HELP_ADD_IDENTIFIER = "add";
    public static final String HELP_DEV_IDENTIFIER = "dev";
    public static final String HELP_ADVANCED_IDENTIFIER = "advanced"; //todo
    public static final String HELP_ABOUT_IDENTIFIER = "about";

    public static final EmbedBuilder MAIN_HELP_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "RulesLawyer Help",
                            "Hello!\n" +
                                    "\n" +
                                    "RulesLawyer is a Discord bot that helps judges and players answer rules questions related to Magic: The Gathering. After reading this post, feel free to delete it by pressing the button. You can bring this up again by doing /help (and selecting the command associated with RulesLawyer, if there's other options associated with other bots on this server).\n" +
                                    "\n" +
                                    "RulesLawyer is designed to be used like a search engine, where you can not only call it with specific citations, but with search terms. You can still call it with curly braces {{like this}}, roughly analogous to Scryfall, but it is recommended that you use the fancy new /rule command.\n" +
                                    "\n" +
                                    "RulesLawyer not only searches the Comprehensive Rules (CR), but also the Infraction Procedure Guide (IPG), Magic Tournament Rules (MTR), and many other documents. However, it will only find search terms that use words that are actually in the rules documents, so its recommended to not use extra \"filler\" words while searching.\n"
                    ),
                    new DiscordEmbedField(
                            "For example, try the following searches:",
                            "/rule cheating\n" +
                                    "/rule failure to maintain definition\n" +
                                    "/rule two-headed giant poison\n" +
                                    "/rule dungeon\n" +
                                    "/rule \"attacks and isn't blocked\" (where the quotation marks forces it to search for an exact match instead of searching for each word individually)\n" +
                                    "\n" +
                                    "More help files are at \"/help add\" to add this to your own server, \"/help about\" for contact information, or \"/help dev\" for the latest patch notes."
                    )
            )
            .build();
    public static final EmbedBuilder HELP_ABOUT_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "About RulesLawyer",
                            "RulesLawyer is written by Elaine Cao, Level 2 from Calgary, Alberta.\n" +
                                    "\n" +
                                    "RulesLawyer uses material that is copyrighted to Wizards of the Coast and is used under fair use doctrine. Feel free to do whatever with it as long as you credit me.\n" +
                                    "\n" +
                                    "Contact me with bugs, feedback, and other inquiries at @ RulesLawyerBot on Twitter or Oritart#0001 on Discord."
                    )
            )
            .build();
    public static final EmbedBuilder HELP_ADD_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "Add RulesLawyer to your own server",
                            "Add **RulesLawyer** to your own server by using this link: https://discordapp.com/oauth2/authorize?client_id=590184543684788253&scope=bot&permissions=2147838016"
                    )
            )
            .build();
    public static final EmbedBuilder HELP_DEV_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "RulesLawyer patch notes",
                            "Last updated: 2021-07-15\n" +
                                    "Rules version: Adventures of the Forgotton Realms"
                    ),
                    new DiscordEmbedField(
                            "v1.11.0",
                            "Slash commands, finally! /help and /rule are available.\n" +
                                    "{{curly braces}} can still be used to call the bot, but this might be deprecated in the future.\n" +
                                    "Also buttons for deletion and pagination!\n" +
                                    "Please let me know if you think that any of the messages are unclear, or could be worded better.\n" +
                                    "And of course let me know if there's any bugs I missed. (I'm sure there are some)"
                    ),
                    new DiscordEmbedField(
                            "Future Development",
                            "Slack and webapp versions are very close to release.\n" +
                                    "Card searching (with /card) is being worked on. It would have been in this release but this release was already very big and I wanted to get it out.\n" +
                                    "Android version is kind of on hold given that there will be a webapp soon."
                    )
            )
            .build();
    public static final EmbedBuilder HELP_NOT_FOUND_EMBED = new EmbedBuilderBuilder()
            .addFields(
                    new DiscordEmbedField(
                            "Help file not found",
                            "I'm sorry, I don't have that help file. Use \"/help\" to get the main file. If you believe this is a bug, report it at \"/help dev\"."
                    )
            )
            .build();

    private HashMap<String, EmbedBuilder> helpFiles;

    public HelpMessageSearchService() {
        helpFiles = new HashMap<>();
        helpFiles.put(HELP_MAIN_IDENTIFIER, MAIN_HELP_EMBED);
        helpFiles.put(HELP_ADD_IDENTIFIER, HELP_ADD_EMBED);
        helpFiles.put(HELP_ABOUT_IDENTIFIER, HELP_ABOUT_EMBED);
        helpFiles.put(HELP_DEV_IDENTIFIER, HELP_DEV_EMBED);
    }

    public EmbedBuilder getHelpFile() {
        return MAIN_HELP_EMBED;
    }

    public EmbedBuilder getHelpFile(String query) {
        return helpFiles.getOrDefault(query, HELP_NOT_FOUND_EMBED);
    }
}
