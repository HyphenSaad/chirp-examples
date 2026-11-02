import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Scanner;

public class WeatherLookup {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String[] CITIES = {"Lahore", "Karachi", "Islamabad", "Peshawar"};
    private static final double[] LATITUDES = {31.55, 24.86, 33.69, 34.01};
    private static final double[] LONGITUDES = {74.34, 67.01, 73.04, 71.58};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Weather Lookup");
        System.out.println();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        while (true) {
            int index = readMenuChoice(scanner, "Pick a city:", CITIES);
            String city = CITIES[index];
            System.out.println();
            fetchAndShow(client, city, LATITUDES[index], LONGITUDES[index]);
            System.out.println();

            String action = readMenuChoice(scanner, "Action:",
                    new String[]{"Look up another city", "Exit"});
            if (action.equals("Exit")) {
                System.out.println("ℹ Goodbye.");
                scanner.close();
                return;
            }
            System.out.println();
        }
    }

    private static void fetchAndShow(HttpClient client, String city,
            double latitude, double longitude) {
        long start = System.nanoTime();
        String body = null;

        try {
            String url = BASE_URL
                    + "?latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&current_weather=true";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            java.net.http.HttpResponse<String> response =
                    client.send(request, BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                body = response.body();
            }
        } catch (IOException e) {
            // fall through — displayed below
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000L;

        if (body == null || !body.contains("\"current_weather\"")) {
            System.out.println("⚠ Could not fetch weather for " + city + ".");
            return;
        }

        String current = extractObject(body, "current_weather");
        if (current == null) {
            System.out.println("⚠ Could not fetch weather for " + city + ".");
            return;
        }

        Double temp = extractDouble(current, "temperature");
        Double wind = extractDouble(current, "windspeed");
        Integer code = extractInt(current, "weathercode");

        if (temp == null || wind == null || code == null) {
            System.out.println("⚠ Could not fetch weather for " + city + ".");
            return;
        }

        printTitle("Weather — " + city);
        System.out.println("ℹ " + temp + " °C");
        System.out.println("ℹ " + describe(code));
        System.out.println("ℹ Wind " + wind + " km/h");
        System.out.println("ℹ Fetched in " + elapsedMs + " ms");
    }

    // Manual extraction of a nested JSON object. Extremely fragile — this only
    // works for the exact shape this API returns. It does not handle nested
    // braces inside the extracted value, or string values containing braces.
    private static String extractObject(String body, String key) {
        String keyToken = "\"" + key + "\"";
        int keyIndex = body.indexOf(keyToken);
        if (keyIndex < 0) return null;
        int openBrace = body.indexOf('{', keyIndex + keyToken.length());
        if (openBrace < 0) return null;
        int depth = 1;
        for (int i = openBrace + 1; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return body.substring(openBrace, i + 1);
            }
        }
        return null;
    }

    private static Double extractDouble(String body, String key) {
        String token = "\"" + key + "\":";
        int idx = body.indexOf(token);
        if (idx < 0) return null;
        int start = idx + token.length();
        int end = start;
        while (end < body.length()
                && (Character.isDigit(body.charAt(end))
                        || body.charAt(end) == '.'
                        || body.charAt(end) == '-')) {
            end++;
        }
        try {
            return Double.parseDouble(body.substring(start, end));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer extractInt(String body, String key) {
        Double d = extractDouble(body, key);
        return d == null ? null : d.intValue();
    }

    private static String describe(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1, 2, 3 -> "Mainly clear / partly cloudy / overcast";
            case 45, 48 -> "Fog";
            case 51, 53, 55 -> "Drizzle";
            case 61, 63, 65 -> "Rain";
            case 71, 73, 75 -> "Snow";
            case 80, 81, 82 -> "Rain showers";
            case 95 -> "Thunderstorm";
            default -> "Unknown";
        };
    }

    private static int readMenuChoice(Scanner scanner, String title, String[] options) {
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
                if (choice >= 1 && choice <= options.length) return choice - 1;
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
