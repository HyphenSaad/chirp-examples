import static chirp.Chirp.*;

public class GradeCalculator {

    public static void main(String[] args) {
        start();
        title("Grade Calculator");

        int score = readInt("Enter score", 0, 100);
        String grade = letterGrade(score);

        blank();
        success("Score: " + score + " — Grade: " + grade);
        info(gradeMessage(grade));
    }

    // Order matters — highest-first, otherwise every score matches the first branch.
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
}
