import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public class WeatherLookupGui extends Application {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String[] CITIES = {"Lahore", "Karachi", "Islamabad", "Peshawar"};
    private static final double[] LATITUDES = {31.55, 24.86, 33.69, 34.01};
    private static final double[] LONGITUDES = {74.34, 67.01, 73.04, 71.58};

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Weather Lookup");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ComboBox<String> citySelect = new ComboBox<>();
        citySelect.getItems().addAll(CITIES);
        citySelect.setValue("Lahore");
        citySelect.setPrefWidth(140);

        Button lookupButton = new Button("Look up");
        lookupButton.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(24, 24);
        spinner.setVisible(false);

        HBox controls = new HBox(8, citySelect, lookupButton, spinner);
        controls.setAlignment(Pos.CENTER);

        Label temperatureLabel = new Label("—");
        temperatureLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #71717a;");

        Label conditionLabel = new Label("");
        conditionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        Label windLabel = new Label("");
        windLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        Label timingLabel = new Label("");
        timingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        Runnable lookup = () -> {
            int index = java.util.Arrays.asList(CITIES).indexOf(citySelect.getValue());
            spinner.setVisible(true);
            temperatureLabel.setText("Loading…");
            temperatureLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #71717a;");
            conditionLabel.setText("");
            windLabel.setText("");
            timingLabel.setText("");

            Thread t = new Thread(() -> {
                try {
                    String url = BASE_URL
                            + "?latitude=" + LATITUDES[index]
                            + "&longitude=" + LONGITUDES[index]
                            + "&current_weather=true";

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .timeout(Duration.ofSeconds(15))
                            .GET()
                            .build();

                    long start = System.nanoTime();
                    java.net.http.HttpResponse<String> response =
                            client.send(request, BodyHandlers.ofString());
                    long elapsed = (System.nanoTime() - start) / 1_000_000L;

                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        showError(spinner, temperatureLabel);
                        return;
                    }

                    Double temp = extractDouble(response.body(), "\"temperature\":");
                    Double wind = extractDouble(response.body(), "\"windspeed\":");
                    Double code = extractDouble(response.body(), "\"weathercode\":");

                    if (temp == null || wind == null || code == null) {
                        showError(spinner, temperatureLabel);
                        return;
                    }

                    String description = describe(code.intValue());
                    Platform.runLater(() -> {
                        spinner.setVisible(false);
                        temperatureLabel.setText(String.format("%.1f °C", temp));
                        temperatureLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #09090b;");
                        conditionLabel.setText(description);
                        windLabel.setText("Wind " + wind + " km/h");
                        timingLabel.setText("Fetched in " + elapsed + " ms");
                    });
                } catch (Exception e) {
                    showError(spinner, temperatureLabel);
                }
            }, "chirp-weather-thread");
            t.setDaemon(true);
            t.start();
        };

        lookupButton.setOnAction(e -> lookup.run());
        citySelect.valueProperty().addListener((o, old, next) -> lookup.run());

        VBox root = new VBox(16,
                titleLabel, controls, temperatureLabel, conditionLabel, windLabel, timingLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));

        Scene scene = new Scene(root, 480, 400);
        stage.setTitle("Weather Lookup");
        stage.setScene(scene);
        stage.show();

        lookup.run();
    }

    private static void showError(ProgressIndicator spinner, Label temperatureLabel) {
        Platform.runLater(() -> {
            spinner.setVisible(false);
            temperatureLabel.setText("Could not fetch");
            temperatureLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        });
    }

    private static Double extractDouble(String body, String key) {
        int idx = body.indexOf(key);
        if (idx < 0) return null;
        int start = idx + key.length();
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

    public static void main(String[] args) {
        launch(args);
    }
}
