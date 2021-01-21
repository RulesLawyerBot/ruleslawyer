package service;

import init_utils.ManaEmojiService;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static app.ApplicationMain.getKey;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ManaEmojiServiceTest {

    DiscordApi api;
    ManaEmojiService manaEmojiService;

    @Before
    public void setUp() {
        api = new DiscordApiBuilder()
                .setToken(getKey("dev"))
                .login()
                .join();
        manaEmojiService = new ManaEmojiService(api);
        manaEmojiService.getEmojis();
    }

    @Test
    public void getEmojis_VerifyParsedCorrectly() {
        HashMap<String, String> manaSymbolEmojis = manaEmojiService.getEmojis();
        manaSymbolEmojis.forEach((k, v) -> System.out.println(k + " " + v));
        assertThat(manaSymbolEmojis.containsKey("U"), is(true));
        assertThat(manaSymbolEmojis.containsKey("U/R"), is(true));
        assertThat(manaSymbolEmojis.containsKey("2/U"), is(true));
        assertThat(manaSymbolEmojis.containsKey("CHAOS"), is(true));
        assertThat(manaSymbolEmojis.containsKey("1/5"), is(false));
    }

    @Test
    public void replaceEmojis_VerifyEmojisReplaced() {
        String testString = "Narset, Parter of Veils costs {1}{U}{U}.";
        String expectedString = "Narset, Parter of Veils costs <:javacord:783461718180364318><:javacord:783461718490873866><:javacord:783461718490873866>.";
        String outputString = manaEmojiService.replaceManaSymbols(testString);
        assertThat(outputString, is(expectedString));
    }

    @After
    public void cleanUp() {
        api.disconnect();
    }
}
