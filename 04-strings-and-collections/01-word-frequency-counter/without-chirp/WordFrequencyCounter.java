import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class WordFrequencyCounter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printTitle("Word Frequency Counter");

        String text = readNonEmptyLine(scanner, "Enter text:");

        Map<String, String> originalCase = new LinkedHashMap<>();
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (String raw : text.split("\\s+")) {
            String cleaned = raw.replaceAll("[\\p{Punct}]", "");
            if (cleaned.isEmpty()) continue;

            String key = cleaned.toLowerCase();
            originalCase.putIfAbsent(key, cleaned);
            counts.merge(key, 1, Integer::sum);
        }

        int totalWords = counts.values().stream().mapToInt(Integer::intValue).sum();
        String longest = "";
        for (String word : originalCase.values()) {
            if (word.length() > longest.length()) longest = word;
        }

        System.out.println();
        System.out.println("ℹ Total words:    " + totalWords);
        System.out.println("ℹ Unique words:   " + counts.size());
        System.out.println("ℹ Longest word:   " + (longest.isEmpty() ? "—" : longest));
        System.out.println();

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort(Comparator
                .comparing(Map.Entry<String, Integer>::getValue, Comparator.reverseOrder())
                .thenComparing(Map.Entry::getKey));

        List<String[]> rows = new ArrayList<>();
        for (var entry : sorted) {
            if (rows.size() >= 10) break;
            rows.add(new String[]{
                    originalCase.get(entry.getKey()),
                    String.valueOf(entry.getValue()),
                    String.format("%.1f%%", 100.0 * entry.getValue() / totalWords)
            });
        }

        printTable(rows, new String[]{"Word", "Count", "Percentage"});

        scanner.close();
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
