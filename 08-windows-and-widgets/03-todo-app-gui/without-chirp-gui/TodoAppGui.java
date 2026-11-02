import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TodoAppGui extends Application {

    private static final String FILE = "todos.txt";

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Todo List");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField newTaskField = new TextField();
        newTaskField.setPromptText("What needs doing?");
        newTaskField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(newTaskField, Priority.ALWAYS);

        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white;");

        HBox inputRow = new HBox(8, newTaskField, addButton);
        inputRow.setMaxWidth(Double.MAX_VALUE);

        ObservableList<String> tasks = FXCollections.observableArrayList(loadTasks());

        ListView<String> taskList = new ListView<>(tasks);
        taskList.setPrefHeight(300);
        taskList.setMaxWidth(Double.MAX_VALUE);

        Button deleteButton = new Button("Delete selected");
        deleteButton.setDisable(true);
        deleteButton.setStyle("-fx-background-color: transparent; -fx-border-color: #d4d4d8; -fx-border-radius: 6;");

        Runnable addTask = () -> {
            String title = newTaskField.getText().trim();
            if (title.isEmpty()) return;
            tasks.add(title);
            saveAll(tasks);
            newTaskField.clear();
            newTaskField.requestFocus();
        };

        addButton.setOnAction(e -> addTask.run());
        newTaskField.setOnAction(e -> addTask.run());

        deleteButton.setOnAction(e -> {
            int index = taskList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                tasks.remove(index);
                saveAll(tasks);
            }
        });

        taskList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, next) -> deleteButton.setDisable(next == null));

        VBox root = new VBox(12, titleLabel, inputRow, taskList, deleteButton);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Todo List");
        stage.setScene(scene);
        stage.show();
    }

    private static List<String> loadTasks() {
        Path file = Path.of(FILE);
        if (!Files.exists(file)) return new ArrayList<>();
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            System.err.println("Cannot read " + FILE + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void saveAll(ObservableList<String> tasks) {
        try {
            Files.write(Path.of(FILE), tasks);
        } catch (IOException e) {
            System.err.println("Cannot save " + FILE + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
