import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExpenseTracker {

    private static final String FILE = "expenses.log";
    private static final String[] ACTIONS = {
            "Add expense", "View recent (last 10)", "Monthly summary",
            "Search by category", "Exit"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Expense Tracker");

        if (loadLines().isEmpty()) {
            seedDemoExpenses();
            System.out.println("ℹ Created " + FILE + " with sample data.");
        } else {
            System.out.println("ℹ Loaded " + loadLines().size() + " entries from " + FILE + ".");
        }
        System.out.println();

        while (true) {
            String action = readMenuChoice(scanner, "Action:", ACTIONS);

            switch (action) {
                case "Add expense" -> {
                    String category = readNonEmptyLine(scanner, "Category:");
                    double amount = readPositiveDouble(scanner, "Amount:");
                    String note = readLine(scanner, "Note (optional):");
                    String line = LocalDate.now() + "|" + category + "|"
                            + amount + "|" + note;
                    appendLine(line);
                    System.out.println("✓ Expense added.");
                }
                case "View recent (last 10)" -> viewRecent();
                case "Monthly summary" -> monthlySummary();
                case "Search by category" -> searchByCategory(scanner);
                case "Exit" -> {
                    System.out.println("ℹ Goodbye.");
                    scanner.close();
                    return;
                }
            }
            System.out.println();
        }
    }

    private static void seedDemoExpenses() {
        LocalDate today = LocalDate.now();
        appendLine(today + "|Food|450|Lunch at cafe");
        appendLine(today + "|Transport|150|Auto to uni");
        appendLine(today.minusDays(1) + "|Food|350|Dinner");
        appendLine(today.minusDays(1) + "|Books|800|OOP textbook");
        appendLine(today.minusDays(2) + "|Food|250|Breakfast");
    }

    private static List<String> loadLines() {
        Path file = Path.of(FILE);
        if (!Files.exists(file)) return new ArrayList<>();
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            System.err.println("Cannot read " + FILE + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void appendLine(String line) {
        try {
            Files.writeString(Path.of(FILE), line + "\n",
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Cannot append to " + FILE + ": " + e.getMessage());
        }
    }

    private static void viewRecent() {
        List<String> all = loadLines();
        if (all.isEmpty()) {
            System.out.println("ℹ No expenses recorded.");
            return;
        }
        int from = Math.max(0, all.size() - 10);
        printEntries(all.subList(from, all.size()));
    }

    private static void monthlySummary() {
        List<String> lines = loadLines();
        if (lines.isEmpty()) {
            System.out.println("ℹ No expenses recorded.");
            return;
        }

        Map<String, List<Double>> byMonth = new LinkedHashMap<>();
        for (String line : lines) {
            String[] parts = line.split("\\|", 4);
            if (parts.length < 3) continue;
            String month = parts[0].length() >= 7 ? parts[0].substring(0, 7) : parts[0];
            double amount = Double.parseDouble(parts[2]);
            byMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(amount);
        }

        List<String[]> rows = new ArrayList<>();
        for (var entry : byMonth.entrySet()) {
            double total = entry.getValue().stream().mapToDouble(Double::doubleValue).sum();
            rows.add(new String[]{
                    entry.getKey(),
                    String.valueOf(entry.getValue().size()),
                    formatRupees(total)
            });
        }
        rows.sort((a, b) -> a[0].compareTo(b[0]));
        printTable(rows, new String[]{"Month", "Entries", "Total"});
    }

    private static void searchByCategory(Scanner scanner) {
        String category = readNonEmptyLine(scanner, "Category to search:");
        List<String> matches = new ArrayList<>();
        for (String line : loadLines()) {
            String[] parts = line.split("\\|", 4);
            if (parts.length < 3) continue;
            if (parts[1].equalsIgnoreCase(category)) matches.add(line);
        }

        if (matches.isEmpty()) {
            System.out.println("ℹ No matches.");
            return;
        }

        double total = 0;
        for (String line : matches) {
            total += Double.parseDouble(line.split("\\|", 4)[2]);
        }
        printEntries(matches);
        System.out.println();
        System.out.println("ℹ Total: " + formatRupees(total));
    }

    private static void printEntries(List<String> lines) {
        List<String[]> rows = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("\\|", 4);
            if (parts.length < 4) continue;
            rows.add(new String[]{
                    parts[0],
                    parts[1],
                    formatRupees(Double.parseDouble(parts[2])),
                    parts[3]
            });
        }
        printTable(rows, new String[]{"Date", "Category", "Amount", "Note"});
    }

    private static String formatRupees(double amount) {
        return "Rs." + String.format("%,.2f", amount);
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

    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt + " ");
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.err.println("Input stream ended unexpectedly.");
            System.exit(1);
        }
        return scanner.nextLine().trim();
    }

    private static double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value > 0) return value;
                System.out.println("Value must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
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
