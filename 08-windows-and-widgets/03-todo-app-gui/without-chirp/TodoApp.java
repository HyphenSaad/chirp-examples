import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TodoApp {

    private static final String FILE = "todos.txt";
    private static final String[] ACTIONS = {"Add task", "Delete task", "View tasks", "Exit"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Todo App");

        List<String> tasks = loadTasks();

        while (true) {
            String action = readMenuChoice(scanner, "Action:", ACTIONS);

            switch (action) {
                case "Add task" -> {
                    String title = readNonEmptyLine(scanner, "Task title:");
                    tasks.add(title);
                    saveAll(tasks);
                    System.out.println("✓ Task added.");
                }
                case "Delete task" -> {
                    if (tasks.isEmpty()) {
                        System.out.println("✗ No tasks to delete.");
                        break;
                    }
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println("ℹ " + (i + 1) + ") " + tasks.get(i));
                    }
                    int index = readIntInRange(scanner, "Task number:", 1, tasks.size()) - 1;
                    tasks.remove(index);
                    saveAll(tasks);
                    System.out.println("✓ Deleted.");
                }
                case "View tasks" -> {
                    if (tasks.isEmpty()) {
                        System.out.println("ℹ No tasks.");
                    } else {
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println("ℹ " + (i + 1) + ") " + tasks.get(i));
                        }
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

    private static void saveAll(List<String> tasks) {
        try {
            Files.write(Path.of(FILE), tasks);
        } catch (IOException e) {
            System.err.println("Cannot save " + FILE + ": " + e.getMessage());
        }
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

    private static void printTitle(String text) {
        String bar = "=".repeat(40);
        System.out.println(bar);
        System.out.println("│ " + text);
        System.out.println(bar);
    }
}
