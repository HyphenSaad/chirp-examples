import chirp.io.CsvFile;
import chirp.ui.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static chirp.Chirp.*;

public class StudentGradeTrackerGui {

    private static final String FILE = "grades.csv";
    private static final List<String> HEADERS = List.of("name", "subject", "score");

    public static void main(String[] args) {
        Ui.app(() -> {
            List<Map<String, String>> records = CsvFile.readAsRecords(FILE);
            if (records.isEmpty()) {
                records = seed();
                CsvFile.writeRecords(FILE, records, HEADERS);
            }

            final List<Map<String, String>> finalRecords = records;

            Table gradesTable = Table.of("Name", "Subject", "Score");
            gradesTable.rowHeight(28).height(240).fullWidth();
            populate(gradesTable, finalRecords);

            Table perStudentTable = Table.of("Student", "Subjects", "Average");
            perStudentTable.rowHeight(28).height(240).fullWidth();

            Table perSubjectTable = Table.of("Subject", "Grades", "Average");
            perSubjectTable.rowHeight(28).height(240).fullWidth();

            Runnable refreshReports = () -> {
                perStudentTable.clearAll();
                for (String[] row : report(finalRecords, "name", "Subjects")) {
                    perStudentTable.row((Object[]) row);
                }
                perSubjectTable.clearAll();
                for (String[] row : report(finalRecords, "subject", "Grades")) {
                    perSubjectTable.row((Object[]) row);
                }
            };

            Field nameField = Field.of("Student name").required();
            Field subjectField = Field.of("Subject").required();
            Spinner scoreField = Spinner.of(0, 100, 75);

            Button addButton = Button.primary("Add grade").onClick(() -> {
                if (!nameField.isValid() || !subjectField.isValid()) return;
                Map<String, String> row = new LinkedHashMap<>();
                row.put("name", nameField.value());
                row.put("subject", subjectField.value());
                row.put("score", String.valueOf(scoreField.value()));
                finalRecords.add(row);
                CsvFile.writeRecords(FILE, finalRecords, HEADERS);
                populate(gradesTable, finalRecords);
                refreshReports.run();
                nameField.value("");
                subjectField.value("");
            });

            Row form = Row.h(8).add(nameField, subjectField, scoreField, addButton);

            TabPane tabs = TabPane.of()
                    .add("All grades", gradesTable)
                    .add("Per student", perStudentTable)
                    .add("Per subject", perSubjectTable)
                    .fullWidth();

            refreshReports.run();

            Stack root = Stack.v(12).padding(20);
            root.add(
                    Label.heading("Student Grade Tracker"),
                    Label.muted("Add a grade — data saved to " + FILE),
                    form,
                    tabs);

            Ui.show("Student Grade Tracker", root);
        });
    }

    private static void populate(Table table, List<Map<String, String>> records) {
        table.clearAll();
        for (Map<String, String> r : records) {
            table.row(r.get("name"), r.get("subject"), r.get("score"));
        }
    }

    private static List<String[]> report(List<Map<String, String>> records, String key, String countHeader) {
        Map<String, List<Integer>> grouped = new LinkedHashMap<>();
        for (Map<String, String> r : records) {
            grouped.computeIfAbsent(r.get(key), k -> new ArrayList<>())
                    .add(Integer.parseInt(r.get("score")));
        }
        List<String[]> rows = new ArrayList<>();
        for (var e : grouped.entrySet()) {
            double avg = e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);
            rows.add(new String[]{e.getKey(), String.valueOf(e.getValue().size()), decimal(avg, 2)});
        }
        rows.sort(Comparator.comparing(a -> a[0]));
        return rows;
    }

    private static List<Map<String, String>> seed() {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(row("Saad", "Math", 85));
        list.add(row("Saad", "Physics", 78));
        list.add(row("Alice", "Math", 92));
        list.add(row("Alice", "Physics", 88));
        list.add(row("Bob", "Math", 75));
        return list;
    }

    private static Map<String, String> row(String name, String subject, int score) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("subject", subject);
        m.put("score", String.valueOf(score));
        return m;
    }
}