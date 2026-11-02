import static chirp.Chirp.*;
import java.util.ArrayList;
import java.util.List;

public class TodoApp {

    private static final String FILE = "todos.txt";

    public static void main(String[] args) {
        start();
        title("Todo App");

        List<String> tasks = new ArrayList<>(TextFileIO.readLines(FILE));

        while (true) {
            String action = readChoice("Action:", "Add task", "Delete task", "View tasks", "Exit");

            switch (action) {
                case "Add task" -> {
                    String title = readNonEmpty("Task title");
                    tasks.add(title);
                    saveAll(tasks);
                    success("Task added.");
                }
                case "Delete task" -> {
                    if (tasks.isEmpty()) {
                        error("No tasks to delete.");
                        break;
                    }
                    printTasks(tasks);
                    int index = readInt("Task number", 1, tasks.size()) - 1;
                    tasks.remove(index);
                    saveAll(tasks);
                    success("Deleted.");
                }
                case "View tasks" -> {
                    if (tasks.isEmpty()) {
                        info("No tasks.");
                    } else {
                        printTasks(tasks);
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

    private static void printTasks(List<String> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            info((i + 1) + ") " + tasks.get(i));
        }
    }

    private static void saveAll(List<String> tasks) {
        TextFileIO.writeLines(FILE, tasks);
    }
}
