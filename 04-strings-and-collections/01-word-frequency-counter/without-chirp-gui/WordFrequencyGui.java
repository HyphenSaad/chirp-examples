import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WordFrequencyGui extends Application {

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Word Frequency Counter");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Paste a paragraph — the top 10 words appear below");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        TextArea input = new TextArea();
        input.setPromptText("Paste a paragraph here");
        input.setPrefRowCount(6);

        Label statsLabel = new Label("—");
        statsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #71717a;");

        ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();
        TableView<ObservableList<String>> table = new TableView<>(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setFixedCellSize(28);
        table.setPrefHeight(340);

        String[] headers = {"Word", "Count", "Percentage"};
        for (int i = 0; i < headers.length; i++) {
            int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(data -> {
                ObservableList<String> row = data.getValue();
                return new SimpleStringProperty(idx < row.size() ? row.get(idx) : "");
            });
            table.getColumns().add(col);
        }
        VBox.setVgrow(table, Priority.ALWAYS);

        Runnable analyse = () -> {
            String text = input.getText();
            Map<String, String> originalCase = new LinkedHashMap<>();
            Map<String, Integer> counts = new LinkedHashMap<>();

            for (String raw : text.split("\\s+")) {
                String cleaned = raw.replaceAll("[\\p{Punct}]", "");
                if (cleaned.isEmpty()) continue;
                String key = cleaned.toLowerCase();
                originalCase.putIfAbsent(key, cleaned);
                counts.merge(key, 1, Integer::sum);
            }

            rows.clear();
            if (counts.isEmpty()) {
                statsLabel.setText("No words to analyse.");
                return;
            }

            int total = counts.values().stream().mapToInt(Integer::intValue).sum();
            statsLabel.setText("Total: " + total + " words · Unique: " + counts.size());

            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
            sorted.sort(Comparator
                    .comparing(Map.Entry<String, Integer>::getValue, Comparator.reverseOrder())
                    .thenComparing(Map.Entry::getKey));

            for (int i = 0; i < Math.min(10, sorted.size()); i++) {
                var entry = sorted.get(i);
                ObservableList<String> row = FXCollections.observableArrayList(
                        originalCase.get(entry.getKey()),
                        String.valueOf(entry.getValue()),
                        String.format("%.1f%%", 100.0 * entry.getValue() / total));
                rows.add(row);
            }
        };

        input.textProperty().addListener((o, old, next) -> analyse.run());

        VBox root = new VBox(12, titleLabel, subtitle, input, statsLabel, table);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 620, 560);
        stage.setTitle("Word Frequency Counter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
