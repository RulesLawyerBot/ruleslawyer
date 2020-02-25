package chat_platform;

import contract.rules.AbstractRule;
import contract.rules.RuleHeader;
import org.junit.Ignore;
import org.junit.Test;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

import static contract.RequestSource.DISCORD;
import static contract.RequestSource.SLACK;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RulePrinterServiceTest {

    private RuleHeader cheatingRule = TestUtils.getSampleIPGRule();
    private RuleHeader layersRule = TestUtils.getSampleCRRule();
    private RulePrinterService rulePrinterService;

    @Test
    public void printSingleRule_ForDiscord() {
        rulePrinterService = new RulePrinterService(DISCORD);
        String expectedOutput = "**IPG Unsporting Conduct - Cheating**\n" +
                "> Penalty\n" +
                "```Disqualification```\n" +
                "> Definition\n" +
                "```A person breaks a rule defined by the tournament documents, lies to a Tournament Official, or notices an offense committed in their (or a teammate’s) match and does not call attention to it.\n" +
                "Additionally, the offense must meet the following criteria for it to be considered Cheating:\n" +
                "The player must be attempting to gain advantage from their action.\n" +
                "The player must be aware that they are doing something illegal.\n" +
                "If all criteria are not met, the offense is not Cheating and is handled by a different infraction.\n" +
                "Cheating will often appear on the surface as a Game Play Error or Tournament Error, and must be investigated by the judge to make a determination of intent and awareness.```\n" +
                "> Examples\n" +
                "```A. A player alters the results on a match slip without their opponent’s knowledge.\n" +
                "B. A player lies to a tournament official about what happened in a game to make their case stronger.\n" +
                "C. A player allows their opponent to put a creature into the graveyard even though the creature has not been dealt lethal damage.\n" +
                "D. A player notices that their opponent resolved only half of the triggered ability of Sword of Feast and Famine and decides not to call attention to the error.\n" +
                "E. A player peeks at another player’s picks during the draft.\n" +
                "F. A player adds cards to their Sealed Deck pool.\n" +
                "G. A player realizes they have accidentally drawn an extra card, then fails to call a judge in order to avoid a penalty.```";
        assertThat(rulePrinterService.printRule(cheatingRule), is(expectedOutput));
    }

    @Test
    public void printSingleRule_ForSlack() {
        rulePrinterService = new RulePrinterService(SLACK);
        String expectedOutput = "*IPG Unsporting Conduct - Cheating*\n" +
                "> Penalty\n" +
                "```Disqualification```\n" +
                "> Definition\n" +
                "```A person breaks a rule defined by the tournament documents, lies to a Tournament Official, or notices an offense committed in their (or a teammate’s) match and does not call attention to it.\n" +
                "Additionally, the offense must meet the following criteria for it to be considered Cheating:\n" +
                "The player must be attempting to gain advantage from their action.\n" +
                "The player must be aware that they are doing something illegal.\n" +
                "If all criteria are not met, the offense is not Cheating and is handled by a different infraction.\n" +
                "Cheating will often appear on the surface as a Game Play Error or Tournament Error, and must be investigated by the judge to make a determination of intent and awareness.```\n" +
                "> Examples\n" +
                "```A. A player alters the results on a match slip without their opponent’s knowledge.\n" +
                "B. A player lies to a tournament official about what happened in a game to make their case stronger.\n" +
                "C. A player allows their opponent to put a creature into the graveyard even though the creature has not been dealt lethal damage.\n" +
                "D. A player notices that their opponent resolved only half of the triggered ability of Sword of Feast and Famine and decides not to call attention to the error.\n" +
                "E. A player peeks at another player’s picks during the draft.\n" +
                "F. A player adds cards to their Sealed Deck pool.\n" +
                "G. A player realizes they have accidentally drawn an extra card, then fails to call a judge in order to avoid a penalty.```";
        assertThat(rulePrinterService.printRule(cheatingRule), is(expectedOutput));
    }

    @Ignore
    @Test
    public void printMultipleRules_ExpectPrintedInOrder() {
        rulePrinterService = new RulePrinterService(DISCORD);
        List<AbstractRule> input = asList(cheatingRule, layersRule);
        String expectedOutput = "**IPG Unsporting Conduct - Cheating**\n" +
                "> Penalty\n" +
                "```Disqualification```\n" +
                "> Definition\n" +
                "```A person breaks a rule defined by the tournament documents, lies to a Tournament Official, or notices an offense committed in their (or a teammate’s) match and does not call attention to it.\n" +
                "Additionally, the offense must meet the following criteria for it to be considered Cheating:\n" +
                "The player must be attempting to gain advantage from their action.\n" +
                "The player must be aware that they are doing something illegal.\n" +
                "If all criteria are not met, the offense is not Cheating and is handled by a different infraction.\n" +
                "Cheating will often appear on the surface as a Game Play Error or Tournament Error, and must be investigated by the judge to make a determination of intent and awareness.```\n" +
                "> Examples\n" +
                "```A. A player alters the results on a match slip without their opponent’s knowledge.\n" +
                "B. A player lies to a tournament official about what happened in a game to make their case stronger.\n" +
                "C. A player allows their opponent to put a creature into the graveyard even though the creature has not been dealt lethal damage.\n" +
                "D. A player notices that their opponent resolved only half of the triggered ability of Sword of Feast and Famine and decides not to call attention to the error.\n" +
                "E. A player peeks at another player’s picks during the draft.\n" +
                "F. A player adds cards to their Sealed Deck pool.\n" +
                "G. A player realizes they have accidentally drawn an extra card, then fails to call a judge in order to avoid a penalty.```**613. Interaction of Continuous Effects**\n" +
                "> 613.1 The values of an object’s characteristics are determined by starting with the actual object. For a card, that means the values of the characteristics printed on that card. For a token or a copy of a spell or card, that means the values of the characteristics defined by the effect that created it. Then all applicable continuous effects are applied in a series of layers in the following order:\n" +
                "```613.1a Layer 1: Copy effects are applied. See rule 706, “Copying Objects.”\n" +
                "613.1b Layer 2: Control-changing effects are applied.\n" +
                "613.1c Layer 3: Text-changing effects are applied. See rule 612, “Text-Changing Effects.”\n" +
                "613.1d Layer 4: Type-changing effects are applied. These include effects that change an object’s card type, subtype, and/or supertype.\n" +
                "613.1e Layer 5: Color-changing effects are applied.\n" +
                "613.1f Layer 6: Ability-adding effects, ability-removing effects, and effects that say an object can’t have an ability are applied.\n" +
                "613.1g Layer 7: Power- and/or toughness-changing effects are applied.```\n" +
                "> 613.2 Within layers 1–6, apply effects from characteristic-defining abilities first (see rule 604.3), then all other effects in timestamp order (see rule 613.6). Note that dependency may alter the order in which effects are applied within a layer. (See rule 613.7.)\n" +
                "> 613.3 Within layer 7, apply effects in a series of sublayers in the order described below. Within each sublayer, apply effects in timestamp order. (See rule 613.6.) Note that dependency may alter the order in which effects are applied within a sublayer. (See rule 613.7.)\n" +
                "```613.3a Layer 7a: Effects from characteristic-defining abilities that define power and/or toughness are applied. See rule 604.3.\n" +
                "613.3b Layer 7b: Effects that set power and/or toughness to a specific number or value are applied. Effects that refer to the base power and/or toughness of a creature apply in this layer.\n" +
                "613.3c Layer 7c: Effects that modify power and/or toughness (but don’t set power and/or toughness to a specific number or value) are applied.\n" +
                "613.3d Layer 7d: Power and/or toughness changes from counters are applied. See rule 122, “Counters.”\n" +
                "613.3e Layer 7e: Effects that switch a creature’s power and toughness are applied. Such effects take the value of power and apply it to the creature’s toughness, and take the value of toughness and apply it to the creature’s power.```\n" +
                "> 613.4 The application of continuous effects as described by the layer system is continually and automatically performed by the game. All resulting changes to an object’s characteristics are instantaneous.\n" +
                "> 613.5 If an effect should be applied in different layers and/or sublayers, the parts of the effect each apply in their appropriate ones. If an effect starts to apply in one layer and/or sublayer, it will continue to be applied to the same set of objects in each other applicable layer and/or sublayer, even if the ability generating the effect is removed during this process.\n" +
                "> 613.6 Within a layer or sublayer, determining which order effects are applied in is usually done using a timestamp system. An effect with an earlier timestamp is applied before an effect with a later timestamp.\n" +
                "```613.6a A continuous effect generated by a static ability has the same timestamp as the object the static ability is on, or the timestamp of the effect that created the ability, whichever is later.\n" +
                "613.6b A continuous effect generated by the resolution of a spell or ability receives a timestamp at the time it’s created.\n" +
                "613.6c An object receives a timestamp at the time it enters a zone.\n" +
                "613.6d An Aura, Equipment, or Fortification receives a new timestamp at the time it becomes attached to an object or player.\n" +
                "613.6e A permanent receives a new timestamp at the time it turns face up or face down.\n" +
                "613.6f A double-faced permanent receives a new timestamp at the time it transforms.\n" +
                "613.6g A face-up plane card, phenomenon card, or scheme card receives a timestamp at the time it’s turned face up.\n" +
                "613.6h A face-up vanguard card receives a timestamp at the beginning of the game.\n" +
                "613.6i A conspiracy card receives a timestamp at the beginning of the game. If it’s face down, it receives a new timestamp at the time it turns face up.\n" +
                "613.6j If two or more objects would receive a timestamp simultaneously, such as by entering a zone simultaneously or becoming attached simultaneously, the active player determines their relative timestamp order at that time.```\n" +
                "> 613.7 Within a layer or sublayer, determining which order effects are applied in is sometimes done using a dependency system. If a dependency exists, it will override the timestamp system.\n" +
                "```613.7a An effect is said to “depend on” another if (a) it’s applied in the same layer (and, if applicable, sublayer) as the other effect (see rules 613.1 and 613.3); (b) applying the other would change the text or the existence of the first effect, what it applies to, or what it does to any of the things it applies to; and (c) neither effect is from a characteristic-defining ability or both effects are from characteristic-defining abilities. Otherwise, the effect is considered to be independent of the other effect.\n" +
                "613.7b An effect dependent on one or more other effects waits to apply until just after all of those effects have been applied. If multiple dependent effects would apply simultaneously in this way, they’re applied in timestamp order relative to each other. If several dependent effects form a dependency loop, then this rule is ignored and the effects in the dependency loop are applied in timestamp order.\n" +
                "613.7c After each effect is applied, the order of remaining effects is reevaluated and may change if an effect that has not yet been applied becomes dependent on or independent of one or more other effects that have not yet been applied.```\n" +
                "> 613.8 One continuous effect can override another. Sometimes the results of one effect determine whether another effect applies or what another effect does.\n" +
                "> 613.9 Some continuous effects affect players rather than objects. For example, an effect might give a player protection from red. All such effects are applied in timestamp order after the determination of objects’ characteristics. See also the rules for timestamp order and dependency (rules 613.6 and 613.7).\n" +
                "> 613.10 Some continuous effects affect game rules rather than objects. For example, effects may modify a player’s maximum hand size, or say that a creature must attack this turn if able. These effects are applied after all other continuous effects have been applied. Continuous effects that affect the costs of spells or abilities are applied according to the order specified in rule 601.2f. All other such effects are applied in timestamp order. See also the rules for timestamp order and dependency (rules 613.6 and 613.7).\n";
        assertThat(rulePrinterService.printRules(input), is(expectedOutput));
    }
}