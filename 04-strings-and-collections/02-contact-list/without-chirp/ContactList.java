import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ContactList {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Contact List");

        List<String[]> contacts = new ArrayList<>();
        while (true) {
            System.out.print("Name (blank to finish): ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                System.err.println("Input stream ended unexpectedly.");
                System.exit(1);
            }
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) break;

            String phone = readNonEmptyLine(scanner, "Phone:");
            contacts.add(new String[]{name, phone});
            System.out.println();
        }

        if (contacts.isEmpty()) {
            System.out.println("ℹ No contacts.");
            scanner.close();
            return;
        }

        contacts.sort(Comparator.comparing(row -> row[0].toLowerCase()));

        System.out.println();
        System.out.println("ℹ All contacts (" + contacts.size() + "):");
        System.out.println();
        printTable(contacts, new String[]{"Name", "Phone"});

        System.out.println();
        System.out.print("Search query: ");
        String query = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        String queryLower = query.toLowerCase();
        List<String[]> matches = new ArrayList<>();
        for (String[] row : contacts) {
            if (row[0].toLowerCase().contains(queryLower)) matches.add(row);
        }
        printResults(matches);

        System.out.println();
        System.out.print("Prefix: ");
        String prefix = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        String prefixLower = prefix.toLowerCase();
        List<String[]> byPrefix = new ArrayList<>();
        for (String[] row : contacts) {
            if (row[0].toLowerCase().startsWith(prefixLower)) byPrefix.add(row);
        }
        printResults(byPrefix);

        scanner.close();
    }

    private static void printResults(List<String[]> rows) {
        System.out.println();
        if (rows.isEmpty()) {
            System.out.println("ℹ No matches.");
            return;
        }
        System.out.println("ℹ Matches (" + rows.size() + "):");
        System.out.println();
        printTable(rows, new String[]{"Name", "Phone"});
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
