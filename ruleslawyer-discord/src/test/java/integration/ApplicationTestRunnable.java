package integration;

import app.DiscordApplicationMain;

public class ApplicationTestRunnable implements Runnable {

    private String apiKey;

    public ApplicationTestRunnable(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void run() {
        DiscordApplicationMain.main(new String[]{apiKey});
    }
}
