import static chirp.Chirp.*;

import chirp.http.HttpResponse;
import chirp.json.JsonObject;

public class WeatherLookup {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";

    private static final String[] CITIES = {"Lahore", "Karachi", "Islamabad", "Peshawar"};
    private static final double[] LATITUDES = {31.55, 24.86, 33.69, 34.01};
    private static final double[] LONGITUDES = {74.34, 67.01, 73.04, 71.58};

    public static void main(String[] args) {
        start();
        title("Weather Lookup");
        blank();

        while (true) {
            String city = readChoice("Pick a city:", CITIES);
            int index = indexOf(CITIES, city);
            blank();
            fetchAndShow(city, LATITUDES[index], LONGITUDES[index]);
            blank();

            String action = readChoice("Action:", "Look up another city", "Exit");
            if (action.equals("Exit")) {
                info("Goodbye.");
                return;
            }
            blank();
        }
    }

    private static void fetchAndShow(String city, double latitude, double longitude) {
        HttpResponse response = get(BASE_URL)
                .query("latitude", String.valueOf(latitude))
                .query("longitude", String.valueOf(longitude))
                .query("current_weather", "true")
                .send();

        if (response.hasTransportError() || !response.isSuccess()) {
            warn("Could not fetch weather for " + city + ".");
            return;
        }

        JsonObject current = response.asObject().getObject("current_weather");
        if (current.isEmpty() || !current.has("temperature")) {
            warn("Could not fetch weather for " + city + ".");
            return;
        }

        double temp = current.getDouble("temperature");
        double wind = current.getDouble("windspeed");
        int code = current.getInt("weathercode");

        title("Weather — " + city);
        info(temp + " °C");
        info(describe(code));
        info("Wind " + wind + " km/h");
        info("Fetched in " + response.durationMillis() + " ms");
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

    private static int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return -1;
    }
}
