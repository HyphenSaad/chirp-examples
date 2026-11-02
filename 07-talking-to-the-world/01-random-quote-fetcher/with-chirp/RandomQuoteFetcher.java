import static chirp.Chirp.*;

import chirp.http.HttpResponse;
import chirp.json.JsonObject;

public class RandomQuoteFetcher {

    private static final String URL = "https://dummyjson.com/quotes/random";
    private static final String FALLBACK_QUOTE =
            "The best way to predict the future is to invent it.";
    private static final String FALLBACK_AUTHOR = "Alan Kay";

    public static void main(String[] args) {
        start();
        title("Random Quote Fetcher");
        blank();

        fetchAndShow();

        while (true) {
            String action = readChoice("Action:", "Fetch another quote", "Exit");
            if (action.equals("Exit")) {
                info("Goodbye.");
                return;
            }
            blank();
            fetchAndShow();
            blank();
        }
    }

    private static void fetchAndShow() {
        HttpResponse response = fetch(URL);
        if (response.hasTransportError() || !response.isSuccess()) {
            warn("Could not fetch quote — using a local fallback.");
            blank();
            show(FALLBACK_QUOTE, FALLBACK_AUTHOR);
            return;
        }

        JsonObject json = response.asObject();
        String quote = json.getString("quote", "");
        String author = json.getString("author", "");

        if (quote.isBlank() || author.isBlank()) {
            warn("Could not fetch quote — using a local fallback.");
            blank();
            show(FALLBACK_QUOTE, FALLBACK_AUTHOR);
            return;
        }

        show(quote, author);
        info("Fetched in " + response.durationMillis() + " ms");
    }

    private static void show(String quote, String author) {
        info("\"" + quote + "\"");
        info("  — " + author);
    }
}
