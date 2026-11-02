import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class StudentGradeTracker {

    private static final String FILE = "grades.csv";
    private static final String HEADER = "name,subject,score";
    private static final String[] ACTIONS = {
            "Add grade", "List all", "Per-student report", "Per-subject report", "Exit"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Student Grade Tracker");

        List<Map<String, String>> records = loadRecords();
        if (records.isEmpty()) {
            records = seedDemoData();
            saveAll(records);
            System.out.println("ℹ Created " + FILE + " with sample data.");
        } else {
            System.out.println("ℹ Loaded " + records.size() + " grade(s) from " + FILE + ".");
        }
        System.out.println();

        while (true) {
            String action = readMenuChoice(scanner, "Action:", ACTIONS);

            switch (action) {
                case "Add grade" -> {
                    String name = readNonEmptyLine(scanner, "Student name:");
                    String subject = readNonEmptyLine(scanner, "Subject:");
                    int score = readIntInRange(scanner, "Score:", 0, 100);
                    records.add(record(name, subject, score));
                    saveAll(records);
                    System.out.println("✓ Grade added and saved.");
                }
                case "List all" -> listAll(records);
                case "Per-student report" -> reportGrouped(records, "name", "Student", "Subjects");
                case "Per-subject report" -> reportGrouped(records, "subject", "Subject", "Grades");
                case "Exit" -> {
                    System.out.println("ℹ Goodbye.");
                    scanner.close();
                    return;
                }
            }
            System.out.println();
        }
    }

    private static Map<String, String> record(String name, String subject, int score) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("name", name);
        row.put("subject", subject);
        row.put("score", String.valueOf(score));
        return row;
    }

    private static List<Map<String, String>> seedDemoData() {
        List<Map<String, String>> records = new ArrayList<>();
        records.add(record("Saad", "Math", 85));
        records.add(record("Saad", "Physics", 78));
        records.add(record("Alice", "Math", 92));
        records.add(record("Alice", "Physics", 88));
        records.add(record("Bob", "Math", 75));
        return records;
    }

    // Naive CSV parser. Fields containing commas will not round-trip; use quoting
    // (RFC 4180) if you need that. This is what a student writes first.
    private static List<Map<String, String>> loadRecords() {
        List<Map<String, String>> records = new ArrayList<>();
        Path file = Path.of(FILE);
        if (!Files.exists(file)) return records;

        try {
            List<String> lines = Files.readAllLines(file);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 3) continue;
                records.add(record(parts[0], parts[1], Integer.parseInt(parts[2])));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Cannot read " + FILE + ": " + e.getMessage());
        }
        return records;
    }

    private static void saveAll(List<Map<String, String>> records) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(HEADER).append('\n');
            for (Map<String, String> r : records) {
                sb.append(r.get("name")).append(',')
                        .append(r.get("subject")).append(',')
                        .append(r.get("score")).append('\n');
            }
            Files.writeString(Path.of(FILE), sb.toString());
        } catch (IOException e) {
            System.err.println("Cannot save to " + FILE + ": " + e.getMessage());
        }
    }

    private static void listAll(List<Map<String, String>> records) {
        if (records.isEmpty()) {
            System.out.println("ℹ No grades yet.");
            return;
        }
        List<String[]> rows = new ArrayList<>();
        for (Map<String, String> r : records) {
            rows.add(new String[]{r.get("name"), r.get("subject"), r.get("score")});
        }
        printTable(rows, new String[]{"Name", "Subject", "Score"});
    }

    private static void reportGrouped(List<Map<String, String>> records,
            String keyField, String labelHeader, String countHeader) {
        if (records.isEmpty()) {
            System.out.println("ℹ No grades yet.");
            return;
        }

        Map<String, List<Integer>> grouped = new LinkedHashMap<>();
        for (Map<String, String> r : records) {
            grouped.computeIfAbsent(r.get(keyField), k -> new ArrayList<>())
                    .add(Integer.parseInt(r.get("score")));
        }

        List<String[]> rows = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToInt(Integer::intValue).average().orElse(0);
            rows.add(new String[]{
                    entry.getKey(),
                    String.valueOf(entry.getValue().size()),
                    String.format("%.2f", avg)
            });
        }
        rows.sort(Comparator.comparing(row -> row[0]));
        printTable(rows, new String[]{labelHeader, countHeader, "Average"});
    }

    private static String readMenuChoice(Scanner scanner, String title, String[] options) {
        while (true) {
            System.out.println(title);
            for (int i = 0; i < options.length; i++) {
                System.out.println("  " + (i + 1) + ") " + options[i]);
            }
            System.out.print("Your choice [1-" + options.length + "]: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= options.length) return options[choice - 1];
                System.out.println("Please enter a number between 1 and " + options.length + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private static String readNonEmptyLine(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("This field cannot be empty.");
        }
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
