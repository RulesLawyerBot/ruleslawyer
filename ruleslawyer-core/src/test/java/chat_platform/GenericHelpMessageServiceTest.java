package chat_platform;

import org.junit.Before;
import org.junit.Test;

import static chat_platform.GenericHelpMessageService.MAIN_HELP_STRING;
import static chat_platform.GenericHelpMessageService.HELP_ADD_STRING;
import static chat_platform.GenericHelpMessageService.HELP_NOT_FOUND_STRING;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class GenericHelpMessageServiceTest {

    GenericHelpMessageService genericHelpMessageService;

    @Before
    public void setUp() {
        genericHelpMessageService = new GenericHelpMessageService();
    }

    @Test
    public void getBaseHelpFile() {
        assertThat(genericHelpMessageService.getHelpFile(), is(MAIN_HELP_STRING));
    }

    @Test
    public void getExistentHelpFile() {
        assertThat(genericHelpMessageService.getHelpFile("add"), is(HELP_ADD_STRING));
    }

    @Test
    public void getNonexistantHelpFile() {
        assertThat(genericHelpMessageService.getHelpFile("blah"), is(HELP_NOT_FOUND_STRING));
    }
}
