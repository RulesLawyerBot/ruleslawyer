package integration;

import org.junit.Before;
import org.junit.Test;
import utils.DiscordUtils;

public class DiscordIntegrationTest {

    @Before
    public void setUp() {
        String dev = DiscordUtils.getDiscordKey("dev");
        String test = DiscordUtils.getDiscordKey("test");

    }

    @Test
    public void doStuff() {

    }
}
