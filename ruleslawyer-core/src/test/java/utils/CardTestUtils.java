package utils;

import contract.cards.Card;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class CardTestUtils {

    public static List<Card> getSearchSpace() {
        return asList(getSampleCard(), getSampleCreatureCard(), getSamplePlaneswalkerCard());
    }

    public static Card getSampleCard() {
        return new Card(
                "Gifts Ungiven",
                "{3}{U}",
                "Instant",
                "Search your library for up to four cards with different names and reveal them. Target opponent chooses two of those cards. Put the chosen cards into your graveyard and the rest into your hand. Then shuffle your library.",
                singletonList("You can choose to find fewer than four cards if you want. If you find one or two cards, your opponent must choose for them to be put into your graveyard, even if they don’t want to."),
                singletonList("Something Kamigawa"),
                Collections.emptyList(),
                42,
                "",
                singletonList("")
            );
    }

    public static Card getSampleCreatureCard() {
        return new Card(
                "Thalia, Guardian of Thraben",
                "{1}{W}",
                "Legendary Creature - Human Soldier",
                "Noncreature spells cost {1} more to cast.\n" +
                        "2/1",
                asList("Thalia’s ability affects each spell that’s not a creature spell, including your own.",
                        "To determine the total cost of a spell, start with the mana cost or alternative cost you’re paying, add any cost increases, then apply any cost reductions. The converted mana cost of the spell remains unchanged, no matter what the total cost to cast it was."
                ),
                asList("Secret Lair: Thalia", "Dark Ascension"),
                Collections.emptyList(),
                42,
                "",
                singletonList("")
        );
    }

    public static Card getSamplePlaneswalkerCard() {
        return new Card(
                "Narset, Parter of Veils",
                "{1}{U]{U}",
                "Legendary PLaneswalker - Narset",
                "Each opponent can’t draw more than one card each turn.\n" +
                        "\n" +
                        "−2: Look at the top four cards of your library. You may reveal a noncreature, nonland card from among them and put it into your hand. Put the rest on the bottom of your library in a random order.\n" +
                        "\n" +
                        "Starting loyalty: 5",
                asList("Your opponents can each draw a maximum of one card each on each player’s turn. Subsequent card draws during that turn are ignored.",
                        "If an opponent hasn’t drawn any cards in a turn and a spell or ability instructs that player to draw multiple cards, that player will just draw one card. However, if the draws are optional, the player can’t choose to draw, even if they could draw one card this way.",
                        "Narset will “see” cards drawn by opponents earlier in the turn she entered the battlefield, although Narset can’t affect cards drawn before she entered the battlefield. For example, if an opponent draws two cards, then Narset enters the battlefield, that opponent can’t draw more cards that turn, but the two drawn cards are unaffected.",
                        "Replacement effects (such as that of Underrealm Lich or the first ability of Jace, Wielder of Mysteries) can’t be used to replace draws that Narset disallows. However, if an opponent’s first draw is replaced (by Underrealm Lich’s ability, for example), that draw didn’t happen and Narset won’t stop the next draw (which may also be replaced by Underrealm Lich’s ability)."
                ),
                singletonList("War of the Spark"),
                Collections.emptyList(),
                42,
                "",
                singletonList("")
        );
    }

}
