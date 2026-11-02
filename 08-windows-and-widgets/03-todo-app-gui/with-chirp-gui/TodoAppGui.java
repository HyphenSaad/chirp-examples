import chirp.ui.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import static chirp.Chirp.*;

public class TodoAppGui {

    private static final String FILE = "todos.txt";

    public static void main(String[] args) {
        Ui.app(() -> {
            ObservableList<String> tasks = FXCollections.observableArrayList(
                    TextFileIO.readLines(FILE));

            TextField newTaskField = TextField.of().placeholder("What needs doing?");
            Button addButton = Button.primary("Add");
            ListView taskList = ListView.of();
            taskList.setItems(tasks);
            taskList.height(300);
            taskList.fullWidth();
            Button deleteButton = Button.outline("Delete selected");
            deleteButton.disabled(true);

            Runnable addTask = () -> {
                String title = newTaskField.value().trim();
                if (title.isEmpty()) return;
                tasks.add(title);
                saveAll(tasks);
                newTaskField.text("");
                newTaskField.requestFocus();
            };

            addButton.onClick(addTask);
            newTaskField.onEnter(addTask);

            deleteButton.onClick(() -> {
                int index = taskList.getSelectionModel().getSelectedIndex();
                if (index >= 0) {
                    tasks.remove(index);
                    saveAll(tasks);
                }
            });

            taskList.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, next) -> deleteButton.disabled(next == null));

            Row inputRow = Row.h(8).add(newTaskField, addButton);
            HBox.setHgrow(newTaskField, Priority.ALWAYS);
            newTaskField.fullWidth();

            Stack root = Stack.v(12).padding(20);
            root.add(
                    Label.heading("Todo List"),
                    inputRow.fullWidth(),
                    taskList,
                    deleteButton);

            Ui.show("Todo List", root);
        });
    }

    private static void saveAll(ObservableList<String> tasks) {
        TextFileIO.writeLines(FILE, tasks);
    }
}
