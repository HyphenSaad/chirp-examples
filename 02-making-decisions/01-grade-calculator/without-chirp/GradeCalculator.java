import java.util.Scanner;

public class GradeCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Grade Calculator");

        int score = readIntInRange(scanner, "Enter score:", 0, 100);
        String grade = letterGrade(score);

        System.out.println();
        System.out.println("✓ Score: " + score + " — Grade: " + grade);
        System.out.println("ℹ " + gradeMessage(grade));

        scanner.close();
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

    private static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
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
