import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleCalculatorGui extends Application {

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Simple Calculator");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField aField = new TextField();
        aField.setPromptText("First number");
        aField.setPrefWidth(140);

        TextField bField = new TextField();
        bField.setPromptText("Second number");
        bField.setPrefWidth(140);

        ComboBox<String> operator = new ComboBox<>();
        operator.getItems().addAll("+", "-", "*", "/");
        operator.setValue("+");
        operator.setPrefWidth(70);

        HBox inputRow = new HBox(8, aField, bField, operator);
        inputRow.setAlignment(Pos.CENTER);

        Label resultCaption = new Label("Result");
        resultCaption.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        Label resultLabel = new Label("—");
        resultLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #71717a;");

        Runnable compute = () -> {
            try {
                double a = Double.parseDouble(aField.getText().trim());
                double b = Double.parseDouble(bField.getText().trim());
                double result = switch (operator.getValue()) {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "/" -> b == 0 ? Double.NaN : a / b;
                    default -> 0;
                };
                if (Double.isNaN(result)) {
                    resultLabel.setText("Cannot divide by zero");
                    resultLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
                } else {
                    resultLabel.setText(String.format("%.2f", result));
                    resultLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #09090b;");
                }
            } catch (NumberFormatException e) {
                resultLabel.setText("—");
                resultLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #71717a;");
            }
        };

        aField.textProperty().addListener((o, old, next) -> compute.run());
        bField.textProperty().addListener((o, old, next) -> compute.run());
        operator.valueProperty().addListener((o, old, next) -> compute.run());

        VBox root = new VBox(16, titleLabel, inputRow, resultCaption, resultLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));

        Scene scene = new Scene(root, 480, 360);
        stage.setTitle("Simple Calculator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
