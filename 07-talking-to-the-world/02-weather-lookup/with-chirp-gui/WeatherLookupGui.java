import chirp.ui.*;
import javafx.geometry.Pos;
import static chirp.Chirp.*;
import chirp.json.JsonObject;
import chirp.http.HttpResponse;

public class WeatherLookupGui {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String[] CITIES = {"Lahore", "Karachi", "Islamabad", "Peshawar"};
    private static final double[] LATITUDES = {31.55, 24.86, 33.69, 34.01};
    private static final double[] LONGITUDES = {74.34, 67.01, 73.04, 71.58};

    public static void main(String[] args) {
        Ui.app(() -> {
            Select citySelect = Select.of(CITIES).selected("Lahore");
            Label temperatureLabel = Label.heading("—");
            Label conditionLabel = Label.muted("");
            Label windLabel = Label.muted("");
            Label timingLabel = Label.muted("");
            ProgressIndicator spinner = ProgressIndicator.indeterminate().size(24);

            Runnable lookup = () -> {
                int index = indexOf(CITIES, citySelect.value());
                spinner.setVisible(true);
                temperatureLabel.text("Loading…").color("#71717a");
                conditionLabel.text("");
                windLabel.text("");
                timingLabel.text("");

                Ui.runAsync(() -> {
                    HttpResponse response = get(BASE_URL)
                            .query("latitude", String.valueOf(LATITUDES[index]))
                            .query("longitude", String.valueOf(LONGITUDES[index]))
                            .query("current_weather", "true")
                            .send();

                    if (response.hasTransportError() || !response.isSuccess()) {
                        Ui.runOnUiThread(() -> {
                            spinner.setVisible(false);
                            temperatureLabel.text("Could not fetch").color("#dc2626");
                        });
                        return;
                    }

                    JsonObject current = response.asObject().getObject("current_weather");
                    if (current.isEmpty() || !current.has("temperature")) {
                        Ui.runOnUiThread(() -> {
                            spinner.setVisible(false);
                            temperatureLabel.text("Could not fetch").color("#dc2626");
                        });
                        return;
                    }

                    double temp = current.getDouble("temperature");
                    double wind = current.getDouble("windspeed");
                    int code = current.getInt("weathercode");
                    String description = describe(code);
                    long elapsed = response.durationMillis();

                    Ui.runOnUiThread(() -> {
                        spinner.setVisible(false);
                        temperatureLabel.text(decimal(temp, 1) + " °C").color("#09090b");
                        conditionLabel.text(description);
                        windLabel.text("Wind " + wind + " km/h");
                        timingLabel.text("Fetched in " + elapsed + " ms");
                    });
                }, null);
            };

            Button lookupButton = Button.primary("Look up").onClick(() -> lookup.run());
            citySelect.onChange(c -> lookup.run());
            lookup.run();

            Row controls = Row.h(8).add(citySelect, lookupButton, spinner).align(Pos.CENTER);

            Stack root = Stack.v(16).padding(24).align(Pos.CENTER);
            root.add(
                    Label.heading("Weather Lookup"),
                    controls,
                    temperatureLabel,
                    conditionLabel,
                    windLabel,
                    timingLabel);

            Ui.show("Weather Lookup", root);
        });
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
        return 0;
    }
}
