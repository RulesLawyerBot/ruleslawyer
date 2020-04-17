package chat_platform;

import org.junit.Before;
import org.junit.Test;

import static chat_platform.HelpMessageService.MAIN_HELP;
import static chat_platform.HelpMessageService.HELP_ADD;
import static chat_platform.HelpMessageService.NOT_FOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class HelpMessageServiceTest {

    HelpMessageService helpMessageService;

    @Before
    public void setUp() {
        helpMessageService = new HelpMessageService();
    }

    @Test
    public void getBaseHelpFile() {
        assertThat(helpMessageService.getHelpFile(), is(MAIN_HELP));
    }

    @Test
    public void getExistantHelpFile() {
        assertThat(helpMessageService.getHelpFile("add"), is(HELP_ADD));
    }

    @Test
    public void getNonexistantHelpFile() {
        assertThat(helpMessageService.getHelpFile("blah"), is(NOT_FOUND));
    }
}
