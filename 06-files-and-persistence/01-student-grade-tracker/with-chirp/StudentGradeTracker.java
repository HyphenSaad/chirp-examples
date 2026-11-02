import static chirp.Chirp.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentGradeTracker {

    private static final String FILE = "grades.csv";
    private static final List<String> HEADERS = List.of("name", "subject", "score");

    public static void main(String[] args) {
        start();
        title("Student Grade Tracker");

        List<Map<String, String>> records = CsvFile.readAsRecords(FILE);
        if (records.isEmpty()) {
            records = seedDemoData();
            saveAll(records);
            info("Created " + FILE + " with sample data.");
        } else {
            info("Loaded " + records.size() + " grade(s) from " + FILE + ".");
        }
        blank();

        while (true) {
            String action = readChoice("Action:",
                    "Add grade", "List all", "Per-student report", "Per-subject report", "Exit");

            switch (action) {
                case "Add grade" -> {
                    String name = readNonEmpty("Student name");
                    String subject = readNonEmpty("Subject");
                    int score = readInt("Score", 0, 100);
                    records.add(record(name, subject, score));
                    saveAll(records);
                    success("Grade added and saved.");
                }
                case "List all" -> listAll(records);
                case "Per-student report" -> reportGrouped(records, "name", "Student", "Subjects");
                case "Per-subject report" -> reportGrouped(records, "subject", "Subject", "Grades");
                case "Exit" -> {
                    info("Goodbye.");
                    return;
                }
            }
            blank();
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

    private static void saveAll(List<Map<String, String>> records) {
        CsvFile.writeRecords(FILE, records, HEADERS);
    }

    private static void listAll(List<Map<String, String>> records) {
        if (records.isEmpty()) {
            info("No grades yet.");
            return;
        }
        List<String[]> rows = new ArrayList<>();
        for (Map<String, String> r : records) {
            rows.add(new String[]{r.get("name"), r.get("subject"), r.get("score")});
        }
        table(rows, "Name", "Subject", "Score");
    }

    private static void reportGrouped(List<Map<String, String>> records,
            String keyField, String labelHeader, String countHeader) {
        if (records.isEmpty()) {
            info("No grades yet.");
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
                    decimal(avg, 2)
            });
        }
        rows.sort(Comparator.comparing(row -> row[0]));
        table(rows, labelHeader, countHeader, "Average");
    }
}
