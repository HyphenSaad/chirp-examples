import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TodoList {

    private static final String FILE = "todos.txt";
    private static final String[] ACTIONS = {
            "Add task", "Mark done", "Delete task", "List tasks", "Exit"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Todo List");

        List<Map<String, String>> tasks = loadTasks();
        if (tasks.isEmpty()) {
            tasks = seedDemoTasks();
            saveTasks(tasks);
            System.out.println("ℹ Created " + FILE + " with sample data.");
        } else {
            System.out.println("ℹ Loaded " + tasks.size() + " task(s) from " + FILE + ".");
        }
        System.out.println();

        while (true) {
            String action = readMenuChoice(scanner, "Action:", ACTIONS);

            switch (action) {
                case "Add task" -> {
                    String title = readNonEmptyLine(scanner, "Task title:");
                    Map<String, String> task = new LinkedHashMap<>();
                    task.put("title", title);
                    task.put("done", "false");
                    tasks.add(task);
                    saveTasks(tasks);
                    System.out.println("✓ Task added.");
                }
                case "Mark done" -> {
                    if (tasks.isEmpty()) {
                        System.out.println("✗ No tasks to modify.");
                        break;
                    }
                    printTaskList(tasks);
                    int index = readIntInRange(scanner, "Task number:", 1, tasks.size()) - 1;
                    tasks.get(index).put("done", "true");
                    saveTasks(tasks);
                    System.out.println("✓ Marked done.");
                }
                case "Delete task" -> {
                    if (tasks.isEmpty()) {
                        System.out.println("✗ No tasks to modify.");
                        break;
                    }
                    printTaskList(tasks);
                    int index = readIntInRange(scanner, "Task number:", 1, tasks.size()) - 1;
                    tasks.remove(index);
                    saveTasks(tasks);
                    System.out.println("✓ Deleted.");
                }
                case "List tasks" -> {
                    if (tasks.isEmpty()) {
                        System.out.println("ℹ No tasks.");
                    } else {
                        printTaskList(tasks);
                    }
                }
                case "Exit" -> {
                    System.out.println("ℹ Goodbye.");
                    scanner.close();
                    return;
                }
            }
            System.out.println();
        }
    }

    private static List<Map<String, String>> seedDemoTasks() {
        List<Map<String, String>> tasks = new ArrayList<>();
        tasks.add(task("Buy groceries", false));
        tasks.add(task("Write report", true));
        tasks.add(task("Call mom", false));
        return tasks;
    }

    private static Map<String, String> task(String title, boolean done) {
        Map<String, String> t = new LinkedHashMap<>();
        t.put("title", title);
        t.put("done", String.valueOf(done));
        return t;
    }

    // Pipe-delimited format. Titles containing '|' will break parsing —
    // use a proper format (JSON, TSV) if titles need that character.
    private static List<Map<String, String>> loadTasks() {
        List<Map<String, String>> tasks = new ArrayList<>();
        Path file = Path.of(FILE);
        if (!Files.exists(file)) return tasks;

        try {
            for (String line : Files.readAllLines(file)) {
                if (line.isBlank()) continue;
                int pipe = line.indexOf('|');
                if (pipe < 0) continue;
                Map<String, String> t = new LinkedHashMap<>();
                t.put("title", line.substring(0, pipe));
                t.put("done", line.substring(pipe + 1));
                tasks.add(t);
            }
        } catch (IOException e) {
            System.err.println("Cannot read " + FILE + ": " + e.getMessage());
        }
        return tasks;
    }

    private static void saveTasks(List<Map<String, String>> tasks) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Map<String, String> t : tasks) {
                sb.append(t.get("title")).append('|').append(t.get("done")).append('\n');
            }
            Files.writeString(Path.of(FILE), sb.toString());
        } catch (IOException e) {
            System.err.println("Cannot save to " + FILE + ": " + e.getMessage());
        }
    }

    private static void printTaskList(List<Map<String, String>> tasks) {
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Map<String, String> t = tasks.get(i);
            rows.add(new String[]{
                    String.valueOf(i + 1),
                    Boolean.parseBoolean(t.get("done")) ? "✓" : "○",
                    t.get("title")
            });
        }
        printTable(rows, new String[]{"#", "Status", "Title"});
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
