package init_utils;

import app.ApplicationMain;
import contract.rules.AbstractRule;
import contract.rules.Rule;
import contract.rules.RuleHeader;
import contract.rules.RuleSubheader;
import org.javacord.api.DiscordApi;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.isLetter;
import static java.lang.String.join;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;

public class ManaEmojiService {

    DiscordApi api;
    HashMap<String, String> manaSymbolEmojis;

    public ManaEmojiService(DiscordApi api) {
        this.api = api;
        getEmojis();
    }

    public HashMap<String, String> getEmojis() {
        this.manaSymbolEmojis = new HashMap<>();
        api.getServerById(ApplicationMain.DEV_SERVER_ID)
                .get()
                .getCustomEmojis()
                .forEach(emoji -> {
                            String[] rawText = emoji.asCustomEmoji().get().getReactionTag().split(":");
                            String key = rawText[0].substring(4);
                            if (key.length() == 2 && isLetter(key.charAt(1))) {
                                key = key.charAt(0) + "/" + key.charAt(1);
                            }
                            String value = "<:javacord:" + rawText[1] + ">";
                            manaSymbolEmojis.put(key.toUpperCase(), value);
                        }
                        );
        manaSymbolEmojis.remove("lete");
        return manaSymbolEmojis;
    }

    public AbstractRule replaceManaSymbols(AbstractRule rule) {
        if (rule.getClass() == RuleHeader.class) {
            RuleHeader outputRule = new RuleHeader(replaceManaSymbols(rule.getText()), rule.getRuleSource());
            outputRule.addAll(
              rule.getSubRules().stream()
                      .map(this::replaceManaSymbols)
                      .collect(toList())
            );
            return outputRule;
        }
        if (rule.getClass() == RuleSubheader.class) {
            RuleSubheader outputRule = new RuleSubheader(replaceManaSymbols(rule.getText()));
            outputRule.addAll(
                    rule.getSubRules().stream()
                            .map(this::replaceManaSymbols)
                            .collect(toList())
            );
            return outputRule;
        }
        Rule outputRule = new Rule(replaceManaSymbols(rule.getText()));
        outputRule.addAll(
                rule.getSubRules().stream()
                        .map(this::replaceManaSymbols)
                        .collect(toList())
        );
        return outputRule;
    }

    public String replaceManaSymbols(String ruleText) {
        String patternString = "\\{(" + join("|", manaSymbolEmojis.keySet()) + ")\\}";
        Pattern pattern = compile(patternString);
        Matcher matcher = pattern.matcher(ruleText);

        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb, manaSymbolEmojis.get(matcher.group(1)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
