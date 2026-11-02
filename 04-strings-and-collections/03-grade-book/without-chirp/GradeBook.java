import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GradeBook {

    record Grade(String student, String subject, int score) {}

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Grade Book");

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

        System.out.println();
        System.out.println("ℹ Per student:");
        System.out.println();
        List<String[]> perStudent = buildPerStudent(grades);
        printTable(perStudent, new String[]{"Name", "Subjects", "Average"});

        System.out.println();
        System.out.println("ℹ Per subject:");
        System.out.println();
        List<String[]> perSubject = buildPerSubject(grades);
        printTable(perSubject, new String[]{"Subject", "Students", "Average"});

        System.out.println();
        System.out.println("ℹ Leaderboard:");
        System.out.println();
        List<String[]> leaderboard = buildLeaderboard(grades);
        printTable(leaderboard, new String[]{"Rank", "Name", "Average"});

        System.out.println();
        String topName = leaderboard.get(0)[1];
        String topAvg = leaderboard.get(0)[2];
        System.out.println("ℹ Top student: " + topName + " (" + topAvg + ")");

        System.out.println();
        System.out.print("Search student: ");
        String query = scanner.hasNextLine() ? scanner.nextLine().trim().toLowerCase() : "";

        List<String[]> matches = new ArrayList<>();
        for (Grade g : grades) {
            if (g.student().toLowerCase().contains(query)) {
                matches.add(new String[]{
                        g.student(), g.subject(), String.valueOf(g.score())
                });
            }
        }

        System.out.println();
        if (matches.isEmpty()) {
            System.out.println("ℹ No matches.");
        } else {
            printTable(matches, new String[]{"Student", "Subject", "Score"});
        }

        scanner.close();
    }

    private static List<String[]> buildPerStudent(List<Grade> grades) {
        Map<String, List<Grade>> byStudent = grades.stream()
                .collect(Collectors.groupingBy(Grade::student));
        List<String[]> rows = new ArrayList<>();
        for (var entry : byStudent.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToInt(Grade::score).average().orElse(0);
            rows.add(new String[]{
                    entry.getKey(),
                    String.valueOf(entry.getValue().size()),
                    String.format("%.2f", avg)
            });
        }
        rows.sort(Comparator.comparing(row -> row[0]));
        return rows;
    }

    private static List<String[]> buildPerSubject(List<Grade> grades) {
        Map<String, List<Grade>> bySubject = grades.stream()
                .collect(Collectors.groupingBy(Grade::subject));
        List<String[]> rows = new ArrayList<>();
        for (var entry : bySubject.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToInt(Grade::score).average().orElse(0);
            rows.add(new String[]{
                    entry.getKey(),
                    String.valueOf(entry.getValue().size()),
                    String.format("%.2f", avg)
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

    private static void printTable(List<String[]> rows, String[] headers) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (row[i].length() > widths[i]) widths[i] = row[i].length();
            }
        }
        printRow(headers, widths);
        for (int w : widths) System.out.print("-".repeat(w + 2));
        System.out.println();
        for (String[] row : rows) printRow(row, widths);
    }

    private static void printRow(String[] cells, int[] widths) {
        for (int i = 0; i < widths.length; i++) {
            System.out.printf("%-" + (widths[i] + 2) + "s", cells[i]);
        }
        System.out.println();
    }

    private static void printTitle(String text) {
        String bar = "=".repeat(40);
        System.out.println(bar);
        System.out.println("│ " + text);
        System.out.println(bar);
    }
}
