import static chirp.Chirp.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpenseTracker {

    private static final String FILE = "expenses.log";

    public static void main(String[] args) {
        start();
        title("Expense Tracker");

        if (TextFileIO.readAll(FILE).isEmpty()) {
            seedDemoExpenses();
            info("Created " + FILE + " with sample data.");
        } else {
            info("Loaded " + countLines() + " entries from " + FILE + ".");
        }
        blank();

        while (true) {
            String action = readChoice("Action:",
                    "Add expense", "View recent (last 10)", "Monthly summary",
                    "Search by category", "Exit");

            switch (action) {
                case "Add expense" -> {
                    String category = readNonEmpty("Category");
                    double amount = readDouble("Amount").min(0.01).read();
                    String note = readLine("Note (optional)");
                    String line = LocalDate.now() + "|" + category + "|"
                            + amount + "|" + note;
                    TextFileIO.appendLine(FILE, line);
                    success("Expense added.");
                }
                case "View recent (last 10)" -> viewRecent();
                case "Monthly summary" -> monthlySummary();
                case "Search by category" -> searchByCategory();
                case "Exit" -> {
                    info("Goodbye.");
                    return;
                }
            }
            blank();
        }
    }

    private static void seedDemoExpenses() {
        LocalDate today = LocalDate.now();
        TextFileIO.appendLine(FILE, today + "|Food|450|Lunch at cafe");
        TextFileIO.appendLine(FILE, today + "|Transport|150|Auto to uni");
        TextFileIO.appendLine(FILE, today.minusDays(1) + "|Food|350|Dinner");
        TextFileIO.appendLine(FILE, today.minusDays(1) + "|Books|800|OOP textbook");
        TextFileIO.appendLine(FILE, today.minusDays(2) + "|Food|250|Breakfast");
    }

    private static int countLines() {
        return TextFileIO.readLines(FILE).size();
    }

    private static void viewRecent() {
        List<String> lines = TextFileIO.tail(FILE, 10);
        if (lines.isEmpty()) {
            info("No expenses recorded.");
            return;
        }
        printEntries(lines);
    }

    private static void monthlySummary() {
        List<String> lines = TextFileIO.readLines(FILE);
        if (lines.isEmpty()) {
            info("No expenses recorded.");
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
                    rupees(total)
            });
        }
        rows.sort((a, b) -> a[0].compareTo(b[0]));
        table(rows, "Month", "Entries", "Total");
    }

    private static void searchByCategory() {
        String category = readNonEmpty("Category to search");

        List<String> matches = new ArrayList<>();
        for (String line : TextFileIO.readLines(FILE)) {
            String[] parts = line.split("\\|", 4);
            if (parts.length < 3) continue;
            if (parts[1].equalsIgnoreCase(category)) matches.add(line);
        }

        if (matches.isEmpty()) {
            info("No matches.");
            return;
        }

        double total = 0;
        for (String line : matches) {
            total += Double.parseDouble(line.split("\\|", 4)[2]);
        }
        printEntries(matches);
        blank();
        info("Total: " + rupees(total));
    }

    private static void printEntries(List<String> lines) {
        List<String[]> rows = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("\\|", 4);
            if (parts.length < 4) continue;
            rows.add(new String[]{
                    parts[0],
                    parts[1],
                    rupees(Double.parseDouble(parts[2])),
                    parts[3]
            });
        }
        table(rows, "Date", "Category", "Amount", "Note");
    }
}
