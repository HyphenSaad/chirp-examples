import static chirp.Chirp.*;
import java.util.ArrayList;
import java.util.List;

public class TodoList {

    private static final String FILE = "todos.json";

    public static void main(String[] args) {
        start();
        title("Todo List");

        JsonArray tasks = JsonFile.readArray(FILE);
        if (tasks.isEmpty()) {
            tasks = seedDemoTasks();
            JsonFile.writePretty(FILE, tasks);
            info("Created " + FILE + " with sample data.");
        } else {
            info("Loaded " + tasks.size() + " task(s) from " + FILE + ".");
        }
        blank();

        while (true) {
            String action = readChoice("Action:",
                    "Add task", "Mark done", "Delete task", "List tasks", "Exit");

            switch (action) {
                case "Add task" -> {
                    String title = readNonEmpty("Task title");
                    JsonObject task = Json.object().put("title", title).put("done", false);
                    tasks.add(task);
                    JsonFile.writePretty(FILE, tasks);
                    success("Task added.");
                }
                case "Mark done" -> {
                    if (tasks.isEmpty()) {
                        error("No tasks to modify.");
                        break;
                    }
                    printTaskList(tasks);
                    int index = readInt("Task number", 1, tasks.size()) - 1;
                    tasks.get(index).asObject().put("done", true);
                    JsonFile.writePretty(FILE, tasks);
                    success("Marked done.");
                }
                case "Delete task" -> {
                    if (tasks.isEmpty()) {
                        error("No tasks to modify.");
                        break;
                    }
                    printTaskList(tasks);
                    int index = readInt("Task number", 1, tasks.size()) - 1;
                    tasks.removeAt(index);
                    JsonFile.writePretty(FILE, tasks);
                    success("Deleted.");
                }
                case "List tasks" -> {
                    if (tasks.isEmpty()) {
                        info("No tasks.");
                    } else {
                        printTaskList(tasks);
                    }
                }
                case "Exit" -> {
                    info("Goodbye.");
                    return;
                }
            }
            blank();
        }
    }

    private static JsonArray seedDemoTasks() {
        JsonArray tasks = new JsonArray();
        tasks.add(Json.object().put("title", "Buy groceries").put("done", false));
        tasks.add(Json.object().put("title", "Write report").put("done", true));
        tasks.add(Json.object().put("title", "Call mom").put("done", false));
        return tasks;
    }

    private static void printTaskList(JsonArray tasks) {
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            JsonObject task = tasks.get(i).asObject();
            rows.add(new String[]{
                    String.valueOf(i + 1),
                    task.getBoolean("done") ? "✓" : "○",
                    task.getString("title")
            });
        }
        table(rows, "#", "Status", "Title");
    }
}
