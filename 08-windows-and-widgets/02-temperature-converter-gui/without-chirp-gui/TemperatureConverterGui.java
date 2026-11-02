import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TemperatureConverterGui extends Application {

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Temperature Converter");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label hintLabel = new Label("Type a Celsius temperature");
        hintLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        TextField celsiusField = new TextField();
        celsiusField.setPromptText("Celsius");
        celsiusField.setMaxWidth(Double.MAX_VALUE);

        Label resultLabel = new Label("—");
        resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #09090b;");

        celsiusField.textProperty().addListener((obs, old, next) -> {
            try {
                double c = Double.parseDouble(next);
                double f = c * 9.0 / 5.0 + 32.0;
                resultLabel.setText(String.format("%.2f °F", f));
                resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #09090b;");
            } catch (NumberFormatException e) {
                resultLabel.setText("Enter a number");
                resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
            }
        });

        VBox root = new VBox(12, titleLabel, hintLabel, celsiusField, resultLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Temperature Converter");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
