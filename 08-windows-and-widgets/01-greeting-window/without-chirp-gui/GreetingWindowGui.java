import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GreetingWindowGui extends Application {

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Greeting Window");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.setMaxWidth(Double.MAX_VALUE);

        Label greetingLabel = new Label("Hello, stranger!");
        greetingLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label timeLabel = new Label("Good " + timeOfDay() + "!");
        timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            nameField.clear();
            nameField.requestFocus();
        });

        nameField.textProperty().addListener((obs, old, next) -> {
            String display = next.isBlank() ? "stranger" : next.trim();
            greetingLabel.setText("Hello, " + display + "!");
        });

        VBox root = new VBox(12, titleLabel, nameField, greetingLabel, timeLabel, clearButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Greeting Window");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private static String timeOfDay() {
        int h = java.time.LocalTime.now().getHour();
        if (h < 12) return "morning";
        if (h < 17) return "afternoon";
        return "evening";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
