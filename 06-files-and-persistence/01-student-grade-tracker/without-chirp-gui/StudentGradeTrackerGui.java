import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentGradeTrackerGui extends Application {

    private static final String FILE = "grades.csv";
    private static final String HEADER = "name,subject,score";

    @Override
    public void start(Stage stage) {
        List<Map<String, String>> records = loadRecords();
        if (records.isEmpty()) {
            records = seed();
            saveAll(records);
        }

        Label titleLabel = new Label("Student Grade Tracker");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Add a grade — data saved to " + FILE);
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        TextField nameField = new TextField();
        nameField.setPromptText("Student name");
        nameField.setPrefWidth(160);

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");
        subjectField.setPrefWidth(140);

        Spinner<Integer> scoreField = new Spinner<>(0, 100, 75);
        scoreField.setPrefWidth(90);

        Button addButton = new Button("Add grade");
        addButton.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white;");

        HBox form = new HBox(8, nameField, subjectField, scoreField, addButton);

        TableView<ObservableList<String>> gradesTable = buildTable("Name", "Subject", "Score");
        TableView<ObservableList<String>> perStudentTable = buildTable("Student", "Subjects", "Average");
        TableView<ObservableList<String>> perSubjectTable = buildTable("Subject", "Grades", "Average");

        populate(gradesTable, records.stream()
                .map(r -> List.of(r.get("name"), r.get("subject"), r.get("score")))
                .toList());

        List<Map<String, String>> finalRecords = records;

        Runnable refreshReports = () -> {
            populate(perStudentTable, report(finalRecords, "name"));
            populate(perSubjectTable, report(finalRecords, "subject"));
        };

        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String subject = subjectField.getText().trim();
            if (name.isEmpty() || subject.isEmpty()) return;
            Map<String, String> row = new LinkedHashMap<>();
            row.put("name", name);
            row.put("subject", subject);
            row.put("score", String.valueOf(scoreField.getValue()));
            finalRecords.add(row);
            saveAll(finalRecords);
            populate(gradesTable, finalRecords.stream()
                    .map(r -> List.of(r.get("name"), r.get("subject"), r.get("score")))
                    .toList());
            refreshReports.run();
            nameField.clear();
            subjectField.clear();
        });

        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
                new Tab("All grades", gradesTable),
                new Tab("Per student", perStudentTable),
                new Tab("Per subject", perSubjectTable));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        refreshReports.run();

        VBox root = new VBox(12, titleLabel, subtitle, form, tabs);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 720, 560);
        stage.setTitle("Student Grade Tracker");
        stage.setScene(scene);
        stage.show();
    }

    private static TableView<ObservableList<String>> buildTable(String... headers) {
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setFixedCellSize(28);
        for (int i = 0; i < headers.length; i++) {
            int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(data -> {
                ObservableList<String> row = data.getValue();
                return new SimpleStringProperty(idx < row.size() ? row.get(idx) : "");
            });
            table.getColumns().add(col);
        }
        return table;
    }

    private static void populate(TableView<ObservableList<String>> table, List<List<String>> rows) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (List<String> r : rows) data.add(FXCollections.observableArrayList(r));
        table.setItems(data);
    }

    private static List<List<String>> report(List<Map<String, String>> records, String key) {
        Map<String, List<Integer>> grouped = new LinkedHashMap<>();
        for (Map<String, String> r : records) {
            grouped.computeIfAbsent(r.get(key), k -> new ArrayList<>())
                    .add(Integer.parseInt(r.get("score")));
        }
        List<List<String>> rows = new ArrayList<>();
        for (var e : grouped.entrySet()) {
            double avg = e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);
            rows.add(List.of(e.getKey(), String.valueOf(e.getValue().size()),
                    String.format("%.2f", avg)));
        }
        rows.sort(Comparator.comparing(r -> r.get(0)));
        return rows;
    }

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
                Map<String, String> r = new LinkedHashMap<>();
                r.put("name", parts[0]);
                r.put("subject", parts[1]);
                r.put("score", parts[2]);
                records.add(r);
            }
        } catch (IOException e) {
            System.err.println("Cannot read " + FILE + ": " + e.getMessage());
        }
        return records;
    }

    private static void saveAll(List<Map<String, String>> records) {
        try {
            StringBuilder sb = new StringBuilder(HEADER).append('\n');
            for (Map<String, String> r : records) {
                sb.append(r.get("name")).append(',')
                        .append(r.get("subject")).append(',')
                        .append(r.get("score")).append('\n');
            }
            Files.writeString(Path.of(FILE), sb.toString());
        } catch (IOException e) {
            System.err.println("Cannot save " + FILE + ": " + e.getMessage());
        }
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

    public static void main(String[] args) {
        launch(args);
    }
}
