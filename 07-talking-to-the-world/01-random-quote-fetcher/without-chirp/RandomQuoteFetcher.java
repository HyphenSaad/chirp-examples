import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Scanner;

public class RandomQuoteFetcher {

    private static final String URL = "https://dummyjson.com/quotes/random";
    private static final String FALLBACK_QUOTE =
            "The best way to predict the future is to invent it.";
    private static final String FALLBACK_AUTHOR = "Alan Kay";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Random Quote Fetcher");
        System.out.println();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        fetchAndShow(client);

        while (true) {
            String action = readMenuChoice(scanner, "Action:",
                    new String[]{"Fetch another quote", "Exit"});
            if (action.equals("Exit")) {
                System.out.println("ℹ Goodbye.");
                scanner.close();
                return;
            }
            System.out.println();
            fetchAndShow(client);
            System.out.println();
        }
    }

    private static void fetchAndShow(HttpClient client) {
        long start = System.nanoTime();
        String quote = null;
        String author = null;
        long elapsedMs = 0;

        try {
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            java.net.http.HttpResponse<String> response =
                    client.send(request, BodyHandlers.ofString());

            elapsedMs = (System.nanoTime() - start) / 1_000_000L;

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                quote = extractString(response.body(), "\"quote\":");
                author = extractString(response.body(), "\"author\":");
            }
        } catch (IOException e) {
            // Network failure — fall through to the fallback path
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (quote == null || quote.isBlank() || author == null || author.isBlank()) {
            System.out.println("⚠ Could not fetch quote — using a local fallback.");
            System.out.println();
            show(FALLBACK_QUOTE, FALLBACK_AUTHOR);
            return;
        }

        show(quote, author);
        System.out.println("ℹ Fetched in " + elapsedMs + " ms");
    }

    private static void show(String quote, String author) {
        System.out.println("ℹ \"" + quote + "\"");
        System.out.println("ℹ   — " + author);
    }

    // Manual JSON field extraction. Works only for the exact shape this API
    // returns — pretty-printed JSON, spaces around colons, or escaped quotes
    // would all break this. A real JSON parser handles those.
    private static String extractString(String body, String key) {
        int keyIndex = body.indexOf(key);
        if (keyIndex < 0) return "";
        int startQuote = body.indexOf('"', keyIndex + key.length());
        if (startQuote < 0) return "";
        int endQuote = body.indexOf('"', startQuote + 1);
        if (endQuote < 0) return "";
        return body.substring(startQuote + 1, endQuote);
    }

    private static String readMenuChoice(Scanner scanner, String title, String[] options) {
        while (true) {
            System.out.println(title);
            for (int i = 0; i < options.length; i++) {
                System.out.println("  " + (i + 1) + ") " + options[i]);
            }
            System.out.print("Your choice [1-" + options.length + "]: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= options.length) return options[choice - 1];
                System.out.println("Please enter a number between 1 and " + options.length + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private static void printTitle(String text) {
        String bar = "=".repeat(40);
        System.out.println(bar);
        System.out.println("│ " + text);
        System.out.println(bar);
    }
}
