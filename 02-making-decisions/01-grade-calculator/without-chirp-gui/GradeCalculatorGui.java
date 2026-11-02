import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GradeCalculatorGui extends Application {

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Grade Calculator");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label scoreCaption = new Label("Score");
        Spinner<Integer> scoreField = new Spinner<>(0, 100, 75);
        scoreField.setEditable(true);
        scoreField.setPrefWidth(100);

        HBox inputRow = new HBox(12, scoreCaption, scoreField);
        inputRow.setAlignment(Pos.CENTER);

        Label caption = new Label("Your grade");
        caption.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        Label gradeLabel = new Label("C");
        Label messageLabel = new Label("Satisfactory.");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        Runnable update = () -> {
            int score = scoreField.getValue();
            String grade = letterGrade(score);
            gradeLabel.setText(grade);
            gradeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: "
                    + gradeColor(grade) + ";");
            messageLabel.setText(gradeMessage(grade));
        };

        scoreField.valueProperty().addListener((o, old, next) -> update.run());
        update.run();

        VBox root = new VBox(16, titleLabel, inputRow, caption, gradeLabel, messageLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));

        Scene scene = new Scene(root, 400, 340);
        stage.setTitle("Grade Calculator");
        stage.setScene(scene);
        stage.show();
    }

    private static String letterGrade(int score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    private static String gradeMessage(String grade) {
        return switch (grade) {
            case "A" -> "Excellent!";
            case "B" -> "Good work.";
            case "C" -> "Satisfactory.";
            case "D" -> "You passed — barely.";
            default -> "Failing. Please retake.";
        };
    }

    private static String gradeColor(String grade) {
        return switch (grade) {
            case "A", "B" -> "#16a34a";
            case "C" -> "#ca8a04";
            case "D" -> "#ea580c";
            default -> "#dc2626";
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
