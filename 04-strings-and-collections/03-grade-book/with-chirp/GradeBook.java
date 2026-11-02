import static chirp.Chirp.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GradeBook {

    record Grade(String student, String subject, int score) {}

    public static void main(String[] args) {
        start();
        title("Grade Book");

        List<Grade> grades = List.of(
                new Grade("Alice", "Math", 85),
                new Grade("Alice", "Physics", 78),
                new Grade("Alice", "Chemistry", 92),
                new Grade("Bob", "Math", 90),
                new Grade("Bob", "Physics", 88),
                new Grade("Bob", "Chemistry", 76),
                new Grade("Charlie", "Math", 70),
                new Grade("Charlie", "Physics", 65),
                new Grade("Diana", "Math", 95),
                new Grade("Diana", "Physics", 92),
                new Grade("Diana", "Chemistry", 88),
                new Grade("Eve", "Math", 60),
                new Grade("Eve", "Physics", 72),
                new Grade("Eve", "Chemistry", 68)
        );

        blank();
        info("Per student:");
        blank();
        List<String[]> perStudent = buildPerStudent(grades);
        table(perStudent, "Name", "Subjects", "Average");

        blank();
        info("Per subject:");
        blank();
        List<String[]> perSubject = buildPerSubject(grades);
        table(perSubject, "Subject", "Students", "Average");

        blank();
        info("Leaderboard:");
        blank();
        List<String[]> leaderboard = buildLeaderboard(grades);
        table(leaderboard, "Rank", "Name", "Average");

        blank();
        String topName = leaderboard.get(0)[1];
        String topAvg = leaderboard.get(0)[2];
        info("Top student: " + topName + " (" + topAvg + ")");

        blank();
        String query = readLine("Search student");
        List<String[]> matches = new ArrayList<>();
        for (Grade g : filter(grades, g -> contains(g.student(), query))) {
            matches.add(new String[]{
                    g.student(), g.subject(), String.valueOf(g.score())
            });
        }

        blank();
        if (matches.isEmpty()) {
            info("No matches.");
        } else {
            table(matches, "Student", "Subject", "Score");
        }
    }

    private static List<String[]> buildPerStudent(List<Grade> grades) {
        Map<String, List<Grade>> byStudent = Filter.groupBy(grades, Grade::student);
        List<String[]> rows = new ArrayList<>();
        for (var entry : byStudent.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToInt(Grade::score).average().orElse(0);
            rows.add(new String[]{
                    entry.getKey(),
                    String.valueOf(entry.getValue().size()),
                    decimal(avg, 2)
            });
        }
        rows.sort(Comparator.comparing(row -> row[0]));
        return rows;
    }

    private static List<String[]> buildPerSubject(List<Grade> grades) {
        Map<String, List<Grade>> bySubject = Filter.groupBy(grades, Grade::subject);
        List<String[]> rows = new ArrayList<>();
        for (var entry : bySubject.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToInt(Grade::score).average().orElse(0);
            rows.add(new String[]{
                    entry.getKey(),
                    String.valueOf(entry.getValue().size()),
                    decimal(avg, 2)
            });
        }
        rows.sort(Comparator.comparing(row -> row[0]));
        return rows;
    }

    private static List<String[]> buildLeaderboard(List<Grade> grades) {
        List<String[]> perStudent = buildPerStudent(grades);
        perStudent.sort(Comparator.comparing(
                row -> Double.parseDouble(row[2]),
                Comparator.reverseOrder()));
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < perStudent.size(); i++) {
            String[] original = perStudent.get(i);
            rows.add(new String[]{String.valueOf(i + 1), original[0], original[2]});
        }
        return rows;
    }
}
