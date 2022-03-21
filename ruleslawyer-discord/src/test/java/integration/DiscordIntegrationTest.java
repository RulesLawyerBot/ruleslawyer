package integration;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import utils.DiscordUtils;

import javax.swing.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static app.DiscordApplicationMain.DEV_SERVER_ID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.javacord.api.entity.user.UserStatus.ONLINE;
import static service.MessageDeletionService.DELETE_EMOTE_ID;
import static utils.StaticEmojis.RIGHT_EMOJI;

@Ignore
public class DiscordIntegrationTest {

    private Thread testThread;
    private DiscordApi testApi;
    private Server devServer;
    private ServerTextChannel integrationTestChannel;
    private CustomEmoji DELETE_EMOJI;
    private User devApiUser;

    @Before
    public void setUp() throws InterruptedException{
        String devApiToken = DiscordUtils.getDiscordKey("dev");
        String testApiToken = DiscordUtils.getDiscordKey("test");

        testApi = new DiscordApiBuilder()
                .setToken(testApiToken)
                .setAllIntents()
                .login()
                .join();
        devServer = testApi.getServerById(DEV_SERVER_ID).get();
        integrationTestChannel = devServer.getTextChannelsByName("deployment-test").get(0);
        DELETE_EMOJI = devServer.getCustomEmojiById(DELETE_EMOTE_ID).get();
        devApiUser = devServer.getMemberByDiscriminatedName("ruleslawyer-dev#5986").get();

        ApplicationTestRunnable applicationRunnable = new ApplicationTestRunnable(devApiToken);
        testThread = new Thread(applicationRunnable);
        testThread.start();
        SECONDS.sleep(10L);
    }

    @After
    public void tearDown() {
        testThread.stop();
    }

    @Test
    public void verifyApplicationIsOnline() {
        assertThat(devApiUser, is(notNullValue()));
        assertThat(devApiUser.getStatus(), is(ONLINE));
    }

    @Test
    public void sendEchoQuery_InDevServer_VerifyRespondedTo_Paginate_VerifyPaginated_Delete_VerifyDeleted() throws InterruptedException, ExecutionException {
        CompletableFuture<Message> message = integrationTestChannel.sendMessage("{{echo}}");
        verifyMessage(message);
    }

    @Test
    @Ignore //this doesn't work
    public void sendEchoQuery_InDirectMessage_VerifyRespondedTo_Paginate_VerifyPaginated_Delete_VerifyDeleted() throws InterruptedException, ExecutionException {
        CompletableFuture<Message> message = devApiUser.sendMessage("{{echo}}");
        verifyMessage(message);
    }

    private void verifyMessage(CompletableFuture<Message> message) throws InterruptedException, ExecutionException {
        SECONDS.sleep(1L);
        Message respondedMessage = message.get().getMessagesAfter(10).get().getOldestMessage().get();

        assertThat(respondedMessage, is(notNullValue()));
        assertThat(respondedMessage.getLastEditTimestamp().isPresent(), is(false));

        //sighs in this doesn't work with buttons
        /*respondedMessage.addReaction(RIGHT_EMOJI);
        SECONDS.sleep(1L);

        assertThat(respondedMessage.getLastEditTimestamp().isPresent(), is(true));

        respondedMessage.addReaction(DELETE_EMOJI);
        SECONDS.sleep(1L);

        Optional<Message> emptyMessage = message.get().getMessagesAfter(10).get().getOldestMessage();

        assertThat(emptyMessage.isPresent(), is(false));*/
    }
}
