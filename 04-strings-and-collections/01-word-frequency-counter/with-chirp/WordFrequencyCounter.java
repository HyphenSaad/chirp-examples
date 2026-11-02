import static chirp.Chirp.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WordFrequencyCounter {

    public static void main(String[] args) {
        start();
        title("Word Frequency Counter");

        String text = readNonEmpty("Enter text");

        Map<String, String> originalCase = new LinkedHashMap<>();
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (String raw : text.split("\\s+")) {
            String cleaned = removePunctuation(raw);
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

        blank();
        info("Total words:    " + totalWords);
        info("Unique words:   " + counts.size());
        info("Longest word:   " + (longest.isEmpty() ? "—" : longest));
        blank();

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
                    percent(100.0 * entry.getValue() / totalWords, 1)
            });
        }

        table(rows, "Word", "Count", "Percentage");
    }
}
