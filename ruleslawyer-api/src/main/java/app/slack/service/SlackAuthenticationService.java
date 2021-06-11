package app.slack.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static java.lang.String.format;

@Service
public class SlackAuthenticationService {

    private static final String URL = "https://slack.com/api/oauth.v2.access";
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");

    public SlackAuthenticationService() {

    }

    public String authenticate(
            String authenticationCode
    ) throws IOException {
        System.out.println(authenticationCode);
        String outputURL = format("%s?client_id=%s&client_secret=%s&code=%s", URL, CLIENT_ID, CLIENT_SECRET, authenticationCode);
        System.out.println(outputURL);
        URL url = new URL(outputURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String output = s.hasNext() ? s.next() : "";
        System.out.println(output);
        return s.hasNext() ? s.next() : "";
    }
}
