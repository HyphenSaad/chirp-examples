import chirp.ui.*;
import javafx.geometry.Pos;
import static chirp.Chirp.*;

public class GradeCalculatorGui {

    public static void main(String[] args) {
        Ui.app(() -> {
            Spinner scoreField = Spinner.of(0, 100, 75);
            Label gradeLabel = Label.heading("C");
            Label messageLabel = Label.muted("Satisfactory.");

            Runnable update = () -> {
                int score = scoreField.value();
                String grade = letterGrade(score);
                gradeLabel.text(grade).color(gradeColor(grade));
                messageLabel.text(gradeMessage(grade));
            };

            scoreField.onChange(v -> update.run());
            update.run();

            Row inputRow = Row.h(12).add(Label.of("Score"), scoreField).align(Pos.CENTER);

            Stack root = Stack.v(16).padding(24).align(Pos.CENTER);
            root.add(
                    Label.heading("Grade Calculator"),
                    inputRow,
                    Label.muted("Your grade"),
                    gradeLabel,
                    messageLabel);

            Ui.show("Grade Calculator", root);
        });
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
}
