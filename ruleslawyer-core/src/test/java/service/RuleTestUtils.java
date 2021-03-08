package service;

import contract.rules.Rule;
import contract.rules.RuleHeader;
import contract.rules.RuleSubheader;

import static contract.rules.enums.RuleSource.CR;
import static contract.rules.enums.RuleSource.IPG;

public class RuleTestUtils {

    public static RuleHeader getSampleIPGRule() {
        RuleHeader cheatingRule = new RuleHeader("Unsporting Conduct - Cheating", IPG);
        RuleSubheader penalty = new RuleSubheader("Penalty");
        penalty.addAll(
                new Rule("Disqualification")
        );
        RuleSubheader definition = new RuleSubheader("Definition");
        definition.addAll(
                new Rule("A person breaks a rule defined by the tournament documents, lies to a Tournament Official, or notices an offense committed in their (or a teammate’s) match and does not call attention to it."),
                new Rule("Additionally, the offense must meet the following criteria for it to be considered Cheating:\n" +
                        "The player must be attempting to gain advantage from their action.\n" +
                        "The player must be aware that they are doing something illegal."),
                new Rule("If all criteria are not met, the offense is not Cheating and is handled by a different infraction."),
                new Rule("Cheating will often appear on the surface as a Game Play Error or Tournament Error, and must be investigated by the judge to make a determination of intent and awareness.")
        );
        RuleSubheader examples = new RuleSubheader("Examples");
        examples.addAll(
                new Rule("A. A player alters the results on a match slip without their opponent’s knowledge."),
                new Rule("B. A player lies to a tournament official about what happened in a game to make their case stronger."),
                new Rule("C. A player allows their opponent to put a creature into the graveyard even though the creature has not been dealt lethal damage."),
                new Rule("D. A player notices that their opponent resolved only half of the triggered ability of Sword of Feast and Famine and decides not to call attention to the error."),
                new Rule("E. A player peeks at another player’s picks during the draft."),
                new Rule("F. A player adds cards to their Sealed Deck pool."),
                new Rule("G. A player realizes they have accidentally drawn an extra card, then fails to call a judge in order to avoid a penalty.")
        );
        cheatingRule.addAll(penalty, definition, examples);
        return cheatingRule;
    }

    public static RuleHeader getSampleCRRule() {
        RuleHeader layersRule = new RuleHeader("613. Interaction of Continuous Effects", CR);
        RuleSubheader[] subRules = new RuleSubheader[10];
        subRules[0] = new RuleSubheader("613.1 The values of an object’s characteristics are determined by starting with the actual object. For a card, that means the values of the characteristics printed on that card. For a token or a copy of a spell or card, that means the values of the characteristics defined by the effect that created it. Then all applicable continuous effects are applied in a series of layers in the following order:");
        subRules[0].addAll(
                new Rule("613.1a Layer 1: Copy effects are applied. See rule 706, “Copying Objects.”"),
                new Rule("613.1b Layer 2: Control-changing effects are applied."),
                new Rule("613.1c Layer 3: Text-changing effects are applied. See rule 612, “Text-Changing Effects.”"),
                new Rule("613.1d Layer 4: Type-changing effects are applied. These include effects that change an object’s card type, subtype, and/or supertype."),
                new Rule("613.1e Layer 5: Color-changing effects are applied."),
                new Rule("613.1f Layer 6: Ability-adding effects, ability-removing effects, and effects that say an object can’t have an ability are applied."),
                new Rule("613.1g Layer 7: Power- and/or toughness-changing effects are applied.")
        );
        subRules[1] = new RuleSubheader("613.2 Within layers 1–6, apply effects from characteristic-defining abilities first (see rule 604.3), then all other effects in timestamp order (see rule 613.6). Note that dependency may alter the order in which effects are applied within a layer. (See rule 613.7.)");
        subRules[2] = new RuleSubheader("613.3 Within layer 7, apply effects in a series of sublayers in the order described below. Within each sublayer, apply effects in timestamp order. (See rule 613.6.) Note that dependency may alter the order in which effects are applied within a sublayer. (See rule 613.7.)");
        subRules[2].addAll(
                new Rule("613.3a Layer 7a: Effects from characteristic-defining abilities that define power and/or toughness are applied. See rule 604.3."),
                new Rule("613.3b Layer 7b: Effects that set power and/or toughness to a specific number or value are applied. Effects that refer to the base power and/or toughness of a creature apply in this layer."),
                new Rule("613.3c Layer 7c: Effects that modify power and/or toughness (but don’t set power and/or toughness to a specific number or value) are applied."),
                new Rule("613.3d Layer 7d: Power and/or toughness changes from counters are applied. See rule 122, “Counters.”"),
                new Rule("613.3e Layer 7e: Effects that switch a creature’s power and toughness are applied. Such effects take the value of power and apply it to the creature’s toughness, and take the value of toughness and apply it to the creature’s power.")
        );
        subRules[3] = new RuleSubheader("613.4 The application of continuous effects as described by the layer system is continually and automatically performed by the game. All resulting changes to an object’s characteristics are instantaneous.");
        subRules[4] = new RuleSubheader("613.5 If an effect should be applied in different layers and/or sublayers, the parts of the effect each apply in their appropriate ones. If an effect starts to apply in one layer and/or sublayer, it will continue to be applied to the same set of objects in each other applicable layer and/or sublayer, even if the ability generating the effect is removed during this process.");
        subRules[5] = new RuleSubheader("613.6 Within a layer or sublayer, determining which order effects are applied in is usually done using a timestamp system. An effect with an earlier timestamp is applied before an effect with a later timestamp.");
        subRules[5].addAll(
                new Rule("613.6a A continuous effect generated by a static ability has the same timestamp as the object the static ability is on, or the timestamp of the effect that created the ability, whichever is later."),
                new Rule("613.6b A continuous effect generated by the resolution of a spell or ability receives a timestamp at the time it’s created."),
                new Rule("613.6c An object receives a timestamp at the time it enters a zone."),
                new Rule("613.6d An Aura, Equipment, or Fortification receives a new timestamp at the time it becomes attached to an object or player."),
                new Rule("613.6e A permanent receives a new timestamp at the time it turns face up or face down."),
                new Rule("613.6f A double-faced permanent receives a new timestamp at the time it transforms."),
                new Rule("613.6g A face-up plane card, phenomenon card, or scheme card receives a timestamp at the time it’s turned face up."),
                new Rule("613.6h A face-up vanguard card receives a timestamp at the beginning of the game."),
                new Rule("613.6i A conspiracy card receives a timestamp at the beginning of the game. If it’s face down, it receives a new timestamp at the time it turns face up."),
                new Rule("613.6j If two or more objects would receive a timestamp simultaneously, such as by entering a zone simultaneously or becoming attached simultaneously, the active player determines their relative timestamp order at that time.")
        );
        subRules[6] = new RuleSubheader("613.7 Within a layer or sublayer, determining which order effects are applied in is sometimes done using a dependency system. If a dependency exists, it will override the timestamp system.");
        subRules[6].addAll(
                new Rule("613.7a An effect is said to “depend on” another if (a) it’s applied in the same layer (and, if applicable, sublayer) as the other effect (see rules 613.1 and 613.3); (b) applying the other would change the text or the existence of the first effect, what it applies to, or what it does to any of the things it applies to; and (c) neither effect is from a characteristic-defining ability or both effects are from characteristic-defining abilities. Otherwise, the effect is considered to be independent of the other effect."),
                new Rule("613.7b An effect dependent on one or more other effects waits to apply until just after all of those effects have been applied. If multiple dependent effects would apply simultaneously in this way, they’re applied in timestamp order relative to each other. If several dependent effects form a dependency loop, then this rule is ignored and the effects in the dependency loop are applied in timestamp order."),
                new Rule("613.7c After each effect is applied, the order of remaining effects is reevaluated and may change if an effect that has not yet been applied becomes dependent on or independent of one or more other effects that have not yet been applied.")
        );
        subRules[7] = new RuleSubheader("613.8 One continuous effect can override another. Sometimes the results of one effect determine whether another effect applies or what another effect does.");
        subRules[8] = new RuleSubheader("613.9 Some continuous effects affect players rather than objects. For example, an effect might give a player protection from red. All such effects are applied in timestamp order after the determination of objects’ characteristics. See also the rules for timestamp order and dependency (rules 613.6 and 613.7).");
        subRules[9] = new RuleSubheader("613.10 Some continuous effects affect game rules rather than objects. For example, effects may modify a player’s maximum hand size, or say that a creature must attack this turn if able. These effects are applied after all other continuous effects have been applied. Continuous effects that affect the costs of spells or abilities are applied according to the order specified in rule 601.2f. All other such effects are applied in timestamp order. See also the rules for timestamp order and dependency (rules 613.6 and 613.7).");
        layersRule.addAll(subRules);
        return layersRule;
    }
}
